package dev.ujhhgtg.wekit.features.items.beautify

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.NinePatchDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.graphics.get
import androidx.core.graphics.toColorInt
import com.tencent.mm.ui.widget.MMNeat7extView
import de.robv.android.xposed.XC_MethodHook
import dev.ujhhgtg.wekit.features.api.core.models.MessageType
import dev.ujhhgtg.wekit.features.api.ui.WeChatMessageViewApi
import dev.ujhhgtg.wekit.features.core.ClickableFeature
import dev.ujhhgtg.wekit.features.core.Feature
import dev.ujhhgtg.wekit.preferences.WePrefs.Companion.prefOption
import dev.ujhhgtg.wekit.ui.content.AlertDialogContent
import dev.ujhhgtg.wekit.ui.content.Button
import dev.ujhhgtg.wekit.ui.content.DefaultColumn
import dev.ujhhgtg.wekit.ui.content.TextButton
import dev.ujhhgtg.wekit.ui.utils.findViewByChildIndexes
import dev.ujhhgtg.wekit.ui.utils.findViewWhich
import dev.ujhhgtg.wekit.ui.utils.showComposeDialog
import dev.ujhhgtg.wekit.utils.android.isDarkMode
import dev.ujhhgtg.wekit.utils.android.showToast
import dev.ujhhgtg.wekit.utils.fs.KnownPaths
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.io.path.absolutePathString
import kotlin.io.path.div
import kotlin.io.path.exists

@Feature(name = "自定义消息气泡", categories = ["界面美化", "聊天"], description = "自定义聊天中的消息气泡图片和颜色")
object CustomMessageBubbles : ClickableFeature(), WeChatMessageViewApi.ICreateViewListener {

    override fun onEnable() {
        WeChatMessageViewApi.addListener(this)
    }

    override fun onDisable() {
        WeChatMessageViewApi.removeListener(this)
    }

    private var thatLight by prefOption("custom_bubbles_color_that_light", "black")
    private var thatDark by prefOption("custom_bubbles_color_that_dark", "white")
    private var thisLight by prefOption("custom_bubbles_color_this_light", "black")
    private var thisDark by prefOption("custom_bubbles_color_this_dark", "black")

    private var bgThatLight by prefOption("custom_bubbles_bg_that_light", "#00000000")
    private var bgThatDark by prefOption("custom_bubbles_bg_that_dark", "#00000000")
    private var bgThisLight by prefOption("custom_bubbles_bg_this_light", "#00000000")
    private var bgThisDark by prefOption("custom_bubbles_bg_this_dark", "#00000000")

    private data class Range(val start: Int, val end: Int)

    private fun getRanges(bitmap: Bitmap, z: Boolean, z2: Boolean): ArrayList<Range> {
        val width = if (z) bitmap.width else bitmap.height
        val i = width - 1
        var i2 = -1
        return ArrayList<Range>().apply {
            for (i3 in 1 until i) {
                val pixel = if (z && z2) {
                    bitmap[i3, bitmap.height - 1]
                } else if (z) {
                    bitmap[i3, 0]
                } else {
                    if (z2) bitmap[bitmap.width - 1, i3] else bitmap[0, i3]
                }
                val iAlpha = Color.alpha(pixel)
                val iRed = Color.red(pixel)
                val iGreen = Color.green(pixel)
                val iBlue = Color.blue(pixel)
                if (iAlpha == 255 && iRed == 0 && iGreen == 0 && iBlue == 0) {
                    if (i2 == -1) {
                        i2 = i3 - 1
                    }
                } else if (i2 != -1) {
                    add(Range(i2, i3 - 1))
                    i2 = -1
                }
            }
            if (i2 != -1) {
                add(Range(i2, width - 2))
            }
        }
    }

    override fun onCreateView(param: XC_MethodHook.MethodHookParam, view: View) {
        val msgInfo = WeChatMessageViewApi.getMsgInfoFromParam(param)

        @Suppress("DEPRECATION")
        when (msgInfo.type) {
            MessageType.TEXT, MessageType.LINK, MessageType.GROUP_NOTE, MessageType.QUOTE -> {
                val neatTextView = view.findViewWhich<MMNeat7extView> { it is MMNeat7extView }!!
                applyForegroundColor(neatTextView, msgInfo.isSelfSender)
                applyBubble(neatTextView, msgInfo.isSelfSender)
            }

            MessageType.VOIP -> {
                val bubbleView = view.findViewWhich<LinearLayout> {
                    it.javaClass == LinearLayout::class.java
                            && it.tag?.javaClass?.name?.startsWith("com.tencent.mm.ui.chatting.viewitems") == true
                }!!
                val bubbleTextView = bubbleView.findViewByChildIndexes<TextView>(0)!!
                val bubbleIconView = bubbleView.findViewByChildIndexes<LinearLayout>(1)!!
                applyForegroundColor(bubbleTextView, msgInfo.isSelfSender)
                applyForegroundColor(bubbleIconView, msgInfo.isSelfSender)
                applyBubble(bubbleView, msgInfo.isSelfSender)
            }

            MessageType.VOICE -> {
                val bubbleView = view.findViewWhich<TextView> {
                    it.javaClass == TextView::class.java
                            && it.tag?.javaClass?.name?.startsWith("com.tencent.mm.ui.chatting.viewitems") == true
                }!!
                applyForegroundColor(bubbleView, msgInfo.isSelfSender)
                applyBubble(bubbleView, msgInfo.isSelfSender)
            }

            else -> {}
        }
    }

    private fun applyBubble(bubbleView: View, isSelfSender: Boolean) {
        val context = bubbleView.context

        val rawColor = if (isSelfSender) {
            if (context.isDarkMode) bgThisDark else bgThisLight
        } else {
            if (context.isDarkMode) bgThatDark else bgThatLight
        }
        val color = runCatching { rawColor.toColorInt() }.getOrDefault(0)

        val fileName = if (isSelfSender) "right_bubble.9.png" else "left_bubble.9.png"
        val file = KnownPaths.moduleAssets / fileName

        val bitmap = if (file.exists()) {
            runCatching { BitmapFactory.decodeFile(file.absolutePathString()) }.getOrNull()
        } else null

        if (bitmap == null) {
            if (color != 0) {
                bubbleView.background?.mutate()?.setTint(color)
            }
            return
        }

        val paddingLeft = bubbleView.paddingLeft
        val paddingTop = bubbleView.paddingTop
        val paddingRight = bubbleView.paddingRight
        val paddingBottom = bubbleView.paddingBottom
        val resources = bubbleView.resources

        val bitmapCreateBitmap = Bitmap.createBitmap(bitmap, 1, 1, bitmap.width - 2, bitmap.height - 2)
        val arrayList1 = getRanges(bitmap, z = true, z2 = false)
        val arrayList2 = getRanges(bitmap, z = false, z2 = false)
        val range1 = getRanges(bitmap, z = true, z2 = true).firstOrNull()
        val range2 = getRanges(bitmap, z = false, z2 = true).firstOrNull()
        val rect = Rect(
            range1?.start ?: 0,
            range2?.start ?: 0,
            if (range1 != null) bitmap.width - 2 - range1.end else 0,
            if (range2 != null) bitmap.height - 2 - range2.end else 0
        )

        val byteBuffer = ByteBuffer.allocate((arrayList2.size + arrayList1.size) * 8 + 68).apply {
            order(ByteOrder.nativeOrder())
            put(1.toByte())
            put((arrayList1.size * 2).toByte())
            put((arrayList2.size * 2).toByte())
            put(9.toByte())
            putInt(0)
            putInt(0)
            putInt(rect.left)
            putInt(rect.right)
            putInt(rect.top)
            putInt(rect.bottom)
            putInt(0)
            for (r in arrayList1) {
                putInt(r.start)
                putInt(r.end)
            }
            for (r in arrayList2) {
                putInt(r.start)
                putInt(r.end)
            }
            repeat(9) {
                putInt(1)
            }
        }

        val ninePatchDrawable = NinePatchDrawable(resources, bitmapCreateBitmap, byteBuffer.array(), rect, null).apply {
            if (color != 0) setTint(color)
        }

        val stateListDrawable = StateListDrawable().apply {
            ninePatchDrawable.constantState?.let { constantState ->
                val drawableMutate = constantState.newDrawable().mutate().apply {
                    val fArr = FloatArray(3).apply {
                        Color.colorToHSV(if (color != 0) color else -1, this)
                        this[2] *= 0.8f
                    }
                    setTint(Color.HSVToColor(fArr))
                }
                addState(intArrayOf(android.R.attr.state_pressed), drawableMutate)
                addState(intArrayOf(android.R.attr.state_focused), drawableMutate)
                addState(intArrayOf(), ninePatchDrawable)
            }
        }

        bubbleView.apply {
            background = stateListDrawable
            setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        }
    }

    private fun getForegroundColor(context: Context, isSelfSender: Boolean): Int {
        val rawColor = if (isSelfSender) {
            if (context.isDarkMode) thisDark else thisLight
        } else {
            if (context.isDarkMode) thatDark else thatLight
        }
        val color = runCatching { rawColor.toColorInt() }.getOrElse {
            showToast(context, "有气泡文本颜色解析失败! 请检查格式")
            -1
        }
        return color
    }

    private fun applyForegroundColor(view: MMNeat7extView, isSelfSender: Boolean) {
        val color = getForegroundColor(view.context, isSelfSender)
        if (color == -1) return

        view.setTextColor(color)
    }

    private fun applyForegroundColor(view: TextView, isSelfSender: Boolean) {
        val color = getForegroundColor(view.context, isSelfSender)
        if (color == -1) return

        view.setTextColor(color)
    }

    private fun applyForegroundColor(view: LinearLayout, isSelfSender: Boolean) {
        val color = getForegroundColor(view.context, isSelfSender)
        if (color == -1) return

        view.backgroundTintList = ColorStateList.valueOf(color)
    }

    override fun onClick(context: Context) {
        showComposeDialog(context) {
            var al by remember { mutableStateOf(thatLight) }
            var ad by remember { mutableStateOf(thatDark) }
            var il by remember { mutableStateOf(thisLight) }
            var id by remember { mutableStateOf(thisDark) }

            var bgal by remember { mutableStateOf(bgThatLight) }
            var bgad by remember { mutableStateOf(bgThatDark) }
            var bgil by remember { mutableStateOf(bgThisLight) }
            var bgid by remember { mutableStateOf(bgThisDark) }

            AlertDialogContent(
                title = { Text("自定义消息气泡") },
                text = {
                    DefaultColumn(Modifier.verticalScroll(rememberScrollState())) {
                        TextField(
                            label = { Text("文字颜色 (对方 | 亮色模式)") },
                            value = al,
                            onValueChange = { al = it })
                        TextField(
                            label = { Text("文字颜色 (对方 | 暗色模式)") },
                            value = ad,
                            onValueChange = { ad = it })
                        TextField(
                            label = { Text("文字颜色 (自己 | 亮色模式)") },
                            value = il,
                            onValueChange = { il = it })
                        TextField(
                            label = { Text("文字颜色 (自己 | 暗色模式)") },
                            value = id,
                            onValueChange = { id = it })

                        TextField(
                            label = { Text("背景色 (对方 | 亮色模式)") },
                            value = bgal,
                            onValueChange = { bgal = it })
                        TextField(
                            label = { Text("背景色 (对方 | 暗色模式)") },
                            value = bgad,
                            onValueChange = { bgad = it })
                        TextField(
                            label = { Text("背景色 (自己 | 亮色模式)") },
                            value = bgil,
                            onValueChange = { bgil = it })
                        TextField(
                            label = { Text("背景色 (自己 | 暗色模式)") },
                            value = bgid,
                            onValueChange = { bgid = it })
                    }
                },
                dismissButton = { TextButton(onDismiss) { Text("取消") } },
                confirmButton = {
                    Button(onClick = {
                        thatLight = al
                        thatDark = ad
                        thisLight = il
                        thisDark = id

                        bgThatLight = bgal
                        bgThatDark = bgad
                        bgThisLight = bgil
                        bgThisDark = bgid
                        onDismiss()
                    }) { Text("确定") }
                })
        }
    }
}
