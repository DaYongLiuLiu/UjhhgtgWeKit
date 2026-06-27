package dev.ujhhgtg.wekit.features.items.system

import android.annotation.SuppressLint
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals
import dev.ujhhgtg.wekit.features.core.Feature
import dev.ujhhgtg.wekit.features.core.SwitchFeature
import dev.ujhhgtg.wekit.utils.HostInfo
import dev.ujhhgtg.reflekt.reflekt
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.deleteRecursively

@Feature(name = "禁用微信热更新", categories = ["系统与隐私"], description = "禁止微信热更新, 避免被强制更新到不兼容版本")
object DisableHostHotUpdates : SwitchFeature() {

    @SuppressLint("SdCardPath")
    @OptIn(ExperimentalPathApi::class)
    override fun onEnable() {
        runCatching { Path("/data/data/${HostInfo.packageName}/tinker").deleteRecursively() }

        ShareTinkerInternals::class.reflekt()
            .methods {
                name {
                    it.startsWith("isTinkerEnabled")
                }
            }
            .forEach {
                it.hookBefore {
                    result = false
                }
            }
    }
}
