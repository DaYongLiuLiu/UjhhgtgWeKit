package dev.ujhhgtg.wekit.hooks.items.chat

import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.TextView
import androidx.core.view.isGone
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import dev.ujhhgtg.comptime.This
import dev.ujhhgtg.wekit.dexkit.abc.IResolvesDex
import dev.ujhhgtg.wekit.dexkit.dsl.dexMethod
import dev.ujhhgtg.wekit.hooks.api.net.WeNetSceneApi
import dev.ujhhgtg.wekit.hooks.api.ui.WeChatMessageViewApi
import dev.ujhhgtg.wekit.hooks.core.HookItem
import dev.ujhhgtg.wekit.hooks.core.SwitchHookItem
import dev.ujhhgtg.wekit.utils.WeLogger
import dev.ujhhgtg.wekit.utils.fs.KnownPaths
import dev.ujhhgtg.wekit.utils.reflection.BString
import dev.ujhhgtg.wekit.utils.reflection.asResolver
import dev.ujhhgtg.wekit.utils.reflection.fields
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

@HookItem(
    name = "显示群成员实名尾字",
    categories = ["聊天"],
    description = "在群聊中通过转账接口获取并显示群成员的真实微信昵称"
)
object DisplayGroupMemberRealNames : SwitchHookItem(), IResolvesDex, WeChatMessageViewApi.ICreateViewListener {

    private val TAG = This.Class.simpleName

    /**
     * Integer tag key stamped onto the username [TextView] so in-flight async fetches can
     * detect that the view has been recycled to a different message before posting their update.
     */
    private const val VIEW_TAG_SENDER = 0x7E000001

    private val cacheFile by lazy { KnownPaths.moduleData / "real_names.json" }
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

    /**
     * wxId → real nickname. Persisted across sessions.
     */
    private val realNames = ConcurrentHashMap<String, String>()

    /**
     * Tracks wxIds for which a fetch has already been dispatched this session.
     */
    private val pendingOrQueried = ConcurrentHashMap.newKeySet<String>()

    /**
     * Correlates NetScene instances to their respective wxIds.
     * Uses weak keys to automatically clean up when the NetScene instance gets garbage collected.
     */
    private val sceneToWxId = Collections.synchronizedMap(WeakHashMap<Any, String>())

    /**
     * Tracks live UI targets that need updating as soon as the corresponding async net fetch lands.
     */
    private val pendingViews = ConcurrentHashMap<String, CopyOnWriteArrayList<WeakReference<TextView>>>()

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onEnable() {
        loadCache()
        WeChatMessageViewApi.addListener(this)

        // Hook the response processing / callback method inside NetSceneBeforeTransfer
        methodNetSceneBeforeTransferOnSceneEnd.hookAfter {
            val wxId = sceneToWxId.remove(thisObject) ?: return@hookAfter

            val errType = args.getOrNull(0) as? Int ?: 0
            val errCode = args.getOrNull(1) as? Int ?: 0

            // If network/server failure occurs, allow future binding attempts to retry
            if (errType != 0 || errCode != 0) {
                WeLogger.w(TAG, "fetch failed for $wxId: errType=$errType errCode=$errCode")
                pendingOrQueried.remove(wxId)
                pendingViews.remove(wxId)
                return@hookAfter
            }

            val realName = thisObject.asResolver()
                .firstField().get()!!.asResolver().fields {
                    type = BString
                }[1].get()!! as String

            if (realName.isNotEmpty()) {
                realNames[wxId] = realName
                saveCache()

                // Immediate UI update for any matching view targets currently alive on screen
                val views = pendingViews.remove(wxId)
                views?.forEach { ref ->
                    val textView = ref.get()
                    if (textView != null) {
                        mainHandler.post {
                            if (textView.getTag(VIEW_TAG_SENDER) == wxId) {
                                applyRealName(textView, realName)
                            }
                        }
                    }
                }
            } else {
                // realName == null → deleted/blocked. Clear references but keep it
                // inside pendingOrQueried to suppress duplicate requests this session.
                pendingViews.remove(wxId)
            }
        }
    }

    override fun onDisable() {
        WeChatMessageViewApi.removeListener(this)
    }

    // ── Cache I/O ─────────────────────────────────────────────────────────────

    private fun loadCache() {
        runCatching {
            val file = cacheFile
            if (!file.exists()) return
            val map = Json.decodeFromString<Map<String, String>>(file.readText())
            realNames.putAll(map)
            WeLogger.d(TAG, "loaded ${map.size} cached real names")
        }.onFailure { WeLogger.w(TAG, "failed to load $cacheFile", it) }
    }

    private fun saveCache() {
        runCatching {
            cacheFile.writeText(Json.encodeToString(realNames.toMap()))
        }.onFailure { WeLogger.w(TAG, "failed to save $cacheFile", it) }
    }

    // ── ICreateViewListener ───────────────────────────────────────────────────

    override fun onCreateView(param: XC_MethodHook.MethodHookParam, view: View) {
        val msgInfo = WeChatMessageViewApi.getMsgInfoFromParam(param)
        if (!msgInfo.isInGroupChat) return
        if (msgInfo.isSend != 0) return

        val sender = runCatching { msgInfo.sender }.getOrNull() ?: return
        val talker = runCatching { msgInfo.talker }.getOrNull() ?: return

        val textView = view.tag.asResolver()
            .firstField { name = "userTV"; superclass() }
            .get() as? TextView? ?: return

        if (textView.isGone) return

        // Stamp sender so the async callback can verify recycling state
        textView.setTag(VIEW_TAG_SENDER, sender)

        val cached = realNames[sender]
        if (cached != null) {
            applyRealName(textView, cached)
            return
        }

        if (pendingOrQueried.add(sender)) {
            pendingViews.computeIfAbsent(sender) { CopyOnWriteArrayList() }.add(WeakReference(textView))
            fetchRealName(sender, talker)
        }
    }

    // ── Network fetch ─────────────────────────────────────────────────────────

    private val methodNetSceneBeforeTransferOnSceneEnd by dexMethod {
        searchPackages("com.tencent.mm.plugin.remittance.model")
        matcher {
            declaredClass {
                usingEqStrings("/cgi-bin/mmpay-bin/beforetransfer")
            }

            usingEqStrings("MicroMsg.NetSceneBeforeTransfer", "ret_code: %s, ret_msg: %s")
        }
    }

    private fun fetchRealName(wxId: String, talker: String) {
        runCatching {
            val netSceneClass = methodNetSceneBeforeTransferOnSceneEnd.method.declaringClass

            // Instantiates the NetScene class passing both the sender and the talker (room id)
            val netScene = XposedHelpers.newInstance(netSceneClass, wxId, talker)

            // Track the created scene context before execution
            sceneToWxId[netScene] = wxId

            // Dispatch to WeChat's native worker queue
            WeNetSceneApi.addNetSceneToQueue(netScene)
        }.onFailure { e ->
            WeLogger.e(TAG, "failed to dispatch NetScene tasks for $wxId", e)
            pendingOrQueried.remove(wxId)
            pendingViews.remove(wxId)
        }
    }

    // ── View update ───────────────────────────────────────────────────────────

    private fun applyRealName(textView: TextView, realName: String) {
        val existing = textView.text ?: return
        val base = existing.toString()
        val annotation = " ($realName)"

        if (base.endsWith(annotation)) return

        val sb = SpannableStringBuilder(existing)
        sb.append(annotation)

        val annotStart = base.length
        val annotEnd = sb.length

        sb.setSpan(
            ForegroundColorSpan(0xFF9E9E9E.toInt()),
            annotStart, annotEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        sb.setSpan(
            RelativeSizeSpan(0.9f),
            annotStart, annotEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = sb
    }
}
