package dev.ujhhgtg.wekit.features.items.moments

import dev.ujhhgtg.comptime.This
import dev.ujhhgtg.wekit.features.api.ui.WeMomentsContextMenuApi
import dev.ujhhgtg.wekit.features.core.Feature
import dev.ujhhgtg.wekit.features.core.SwitchFeature
import dev.ujhhgtg.wekit.ui.utils.SendIcon
import dev.ujhhgtg.wekit.ui.utils.ShareIcon
import dev.ujhhgtg.wekit.utils.WeLogger

@Feature(name = "转发 & 一键转发", categories = ["朋友圈"], description = "转发他人的朋友圈")
object ReMoment : SwitchFeature(), WeMomentsContextMenuApi.IMenuItemsProvider {

    private val TAG = This.Class.simpleName

    override fun onEnable() {
        WeMomentsContextMenuApi.addProvider(this)
    }

    override fun onDisable() {
        WeMomentsContextMenuApi.removeProvider(this)
    }

    override fun getMenuItems(): List<WeMomentsContextMenuApi.MenuItem> {
        return listOf(
            WeMomentsContextMenuApi.MenuItem(
                777013,
                "转发",
                ShareIcon,
                { _, _ -> true },
            ) {

            },
            WeMomentsContextMenuApi.MenuItem(
                777013,
                "一键转发",
                SendIcon,
                { _, _ -> true },
            ) { moment ->
                val snsInfo = moment.snsInfo!!
                WeLogger.d(TAG, snsInfo.javaClass.name)
            },
        )
    }
}
