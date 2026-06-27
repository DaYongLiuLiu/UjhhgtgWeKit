package dev.ujhhgtg.wekit.ui.content

import dev.ujhhgtg.comptime.nameOf
import dev.ujhhgtg.wekit.features.core.BaseFeature
import dev.ujhhgtg.wekit.features.core.ClickableFeature
import dev.ujhhgtg.wekit.features.core.FeaturesProvider
import dev.ujhhgtg.wekit.features.core.SwitchFeature
import dev.ujhhgtg.wekit.preferences.WePrefs
import dev.ujhhgtg.wekit.utils.WeLogger

class CategorySettingsScreen(private val categoryName: String) : BasePrefsScreen(categoryName) {

    override fun initPreferences() {
        val targetItems = FeaturesProvider.ALL_HOOK_ITEMS.filter { item ->
            categoryName in item.categories
        }

        if (targetItems.isEmpty()) return

        targetItems.forEach { item ->
            val name = item.name
            val desc = item.description

            when (item) {
                is ClickableFeature -> addClickableItem(item, name, desc)
                is SwitchFeature -> addSwitchItem(item, name, desc)
            }
        }
    }

    private fun addSwitchItem(
        item: SwitchFeature,
        title: String,
        summary: String,
    ) {
        val configKey = item.name
        val initialChecked = WePrefs.getBoolOrFalse(configKey)

        addHookSwitch(
            key = configKey,
            title = title,
            summary = summary,
            initialChecked = initialChecked,
            onBeforeToggle = { context, checked ->
                val allowed = item.onBeforeToggle(checked, context)
                if (allowed) {
                    WePrefs.putBool(configKey, checked)
                    item.isEnabled = checked
                }
                allowed
            },
            bindCompletionCallback = { callback ->
                item.setToggleCompletionCallback {
                    callback(item.isEnabled)
                }
            },
        )
    }

    private fun addClickableItem(
        item: ClickableFeature,
        title: String,
        summary: String,
    ) {
        val configKey = item.name
        val initialChecked = WePrefs.getBoolOrFalse(configKey)

        addHookClickable(
            key = configKey,
            title = title,
            summary = summary,
            showSwitch = !item.noSwitchWidget,
            initialChecked = initialChecked,
            onBeforeToggle = { context, checked ->
                val allowed = item.onBeforeToggle(checked, context)
                if (allowed) {
                    WePrefs.putBool(configKey, checked)
                    item.isEnabled = checked
                }
                allowed
            },
            bindCompletionCallback = { callback ->
                item.setToggleCompletionCallback {
                    callback(item.isEnabled)
                }
            },
            onClick = {
                runCatching {
                    item.onClick(it)
                }.onFailure { WeLogger.e(nameOf(BaseFeature::class), "failed to execute onClick of ${item.displayName}") }
            },
        )
    }
}
