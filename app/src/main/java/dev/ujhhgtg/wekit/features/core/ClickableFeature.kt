package dev.ujhhgtg.wekit.features.core

import android.content.Context
import dev.ujhhgtg.wekit.preferences.WePrefs
import dev.ujhhgtg.wekit.utils.TargetProcesses

abstract class ClickableFeature : SwitchFeature() {

    override fun startup() {
        if (!TargetProcesses.isInMain) return
        _isEnabled = WePrefs.getBoolOrFalse(name)
        if (_isEnabled || alwaysEnabled) enable()
    }

    open val alwaysEnabled: Boolean = false

    open val noSwitchWidget = false

    abstract fun onClick(context: Context)
}
