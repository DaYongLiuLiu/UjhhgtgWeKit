package dev.ujhhgtg.wekit.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.PredictiveBackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.outlined.Account_circle
import com.composables.icons.materialsymbols.outlined.Add_circle
import com.composables.icons.materialsymbols.outlined.Arrow_back
import com.composables.icons.materialsymbols.outlined.Auto_delete
import com.composables.icons.materialsymbols.outlined.Block
import com.composables.icons.materialsymbols.outlined.Brightness_medium
import com.composables.icons.materialsymbols.outlined.Bug_report
import com.composables.icons.materialsymbols.outlined.Build_circle
import com.composables.icons.materialsymbols.outlined.Call
import com.composables.icons.materialsymbols.outlined.Camera
import com.composables.icons.materialsymbols.outlined.Chat
import com.composables.icons.materialsymbols.outlined.Check_circle
import com.composables.icons.materialsymbols.outlined.Checklist
import com.composables.icons.materialsymbols.outlined.Close
import com.composables.icons.materialsymbols.outlined.Colorize
import com.composables.icons.materialsymbols.outlined.Comedy_mask
import com.composables.icons.materialsymbols.outlined.Contact_page
import com.composables.icons.materialsymbols.outlined.Contacts
import com.composables.icons.materialsymbols.outlined.Contrast
import com.composables.icons.materialsymbols.outlined.Delete_forever
import com.composables.icons.materialsymbols.outlined.Download
import com.composables.icons.materialsymbols.outlined.Frame_bug
import com.composables.icons.materialsymbols.outlined.Home
import com.composables.icons.materialsymbols.outlined.Imagesearch_roller
import com.composables.icons.materialsymbols.outlined.Label
import com.composables.icons.materialsymbols.outlined.License
import com.composables.icons.materialsymbols.outlined.Lightbulb_2
import com.composables.icons.materialsymbols.outlined.Movie
import com.composables.icons.materialsymbols.outlined.Newspaper
import com.composables.icons.materialsymbols.outlined.Notifications
import com.composables.icons.materialsymbols.outlined.Package_2
import com.composables.icons.materialsymbols.outlined.Palette
import com.composables.icons.materialsymbols.outlined.Payments
import com.composables.icons.materialsymbols.outlined.Rule_settings
import com.composables.icons.materialsymbols.outlined.Search
import com.composables.icons.materialsymbols.outlined.Settings
import com.composables.icons.materialsymbols.outlined.Style
import com.composables.icons.materialsymbols.outlined.Sync
import com.composables.icons.materialsymbols.outlined.Terminal
import com.composables.icons.materialsymbols.outlined.Tune
import com.composables.icons.materialsymbols.outlined.Update
import com.composables.icons.materialsymbols.outlined.Upload
import com.composables.icons.materialsymbols.outlined.Volunteer_activism
import com.composables.icons.materialsymbols.outlined.Wallpaper
import com.composables.icons.materialsymbols.outlined.Wand_stars
import com.composables.icons.materialsymbols.outlinedfilled.Home
import com.composables.icons.materialsymbols.outlinedfilled.Settings
import com.composables.icons.materialsymbols.outlinedfilled.Tune
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.tencent.mm.ui.LauncherUI
import dev.ujhhgtg.comptime.nameOf
import dev.ujhhgtg.wekit.BuildConfig
import dev.ujhhgtg.wekit.aboutlibraries.AboutLibrariesProvider
import dev.ujhhgtg.wekit.constants.PackageNames
import dev.ujhhgtg.wekit.constants.Preferences
import dev.ujhhgtg.wekit.features.core.BaseFeature
import dev.ujhhgtg.wekit.features.core.ClickableFeature
import dev.ujhhgtg.wekit.features.core.FeaturesProvider
import dev.ujhhgtg.wekit.features.core.SwitchFeature
import dev.ujhhgtg.wekit.features.items.debug.ResetDexCache
import dev.ujhhgtg.wekit.features.items.easter_egg.AprilFools
import dev.ujhhgtg.wekit.features.items.easter_egg.isAprilFools
import dev.ujhhgtg.wekit.preferences.WePrefs
import dev.ujhhgtg.wekit.ui.content.FloatingBottomBar
import dev.ujhhgtg.wekit.ui.content.FloatingBottomBarDefaults
import dev.ujhhgtg.wekit.ui.content.FloatingBottomBarItem
import dev.ujhhgtg.wekit.ui.content.liquid.vibrancy
import dev.ujhhgtg.wekit.ui.utils.GitHubIcon
import dev.ujhhgtg.wekit.ui.utils.TelegramIcon
import dev.ujhhgtg.wekit.ui.utils.theme.AppColorSpec
import dev.ujhhgtg.wekit.ui.utils.theme.AppPaletteStyle
import dev.ujhhgtg.wekit.ui.utils.theme.AppThemeMode
import dev.ujhhgtg.wekit.ui.utils.theme.ModuleTheme
import dev.ujhhgtg.wekit.ui.utils.theme.ThemeSettings
import dev.ujhhgtg.wekit.utils.AppUpdater
import dev.ujhhgtg.wekit.utils.HostInfo
import dev.ujhhgtg.wekit.utils.UpdateResult
import dev.ujhhgtg.wekit.utils.WeLogger
import dev.ujhhgtg.wekit.utils.android.showToastSuspend
import dev.ujhhgtg.wekit.utils.formatEpoch
import dev.ujhhgtg.wekit.utils.openInSystem
import dev.ujhhgtg.wekit.utils.serialization.DefaultJson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.ColorPicker
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.blur
import top.yukonga.miuix.kmp.blur.drawBackdrop
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.preference.WindowDropdownPreference
import top.yukonga.miuix.kmp.squircle.squircleClip
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import top.yukonga.miuix.kmp.window.WindowDialog
import java.time.LocalDate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.compose.material3.Icon as M3Icon
import androidx.compose.material3.Text as M3Text

val LocalComponentActivity = staticCompositionLocalOf<ComponentActivity> { error("not provided") }

@Keep
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(
                LocalContext provides this,
                LocalActivity provides this,
                LocalComponentActivity provides this
            ) {
                // Theme-mode drives the whole Settings surface (miuix + the Material
                // FloatingBottomBar), so resolve dark once and pass it to both wrappers.
                val dark = ThemeSettings.themeMode.resolve()
                ModuleTheme(darkTheme = dark) {
                    SettingsRoot(onFinish = { finish() })
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
//  Feature categories (name -> icon), mirrored from the legacy MainSettingsScreen
// ---------------------------------------------------------------------------

private val FEATURE_CATEGORIES = listOf(
    "聊天" to MaterialSymbols.Outlined.Chat,
    "联系人与群组" to MaterialSymbols.Outlined.Contacts,
    "红包与支付" to MaterialSymbols.Outlined.Payments,
    "朋友圈" to MaterialSymbols.Outlined.Camera,
    "系统与隐私" to MaterialSymbols.Outlined.Wand_stars,
    "音视频通话" to MaterialSymbols.Outlined.Call,
    "通知" to MaterialSymbols.Outlined.Notifications,
    "界面美化" to MaterialSymbols.Outlined.Imagesearch_roller,
    "公众号" to MaterialSymbols.Outlined.Newspaper,
    "小程序" to MaterialSymbols.Outlined.Package_2,
    "视频号" to MaterialSymbols.Outlined.Movie,
    "个人资料" to MaterialSymbols.Outlined.Account_circle,
    "调试" to MaterialSymbols.Outlined.Bug_report,
    "脚本 (JS)" to MaterialSymbols.Outlined.Terminal,
    "脚本 (Java)" to MaterialSymbols.Outlined.Terminal,
    "娱乐" to MaterialSymbols.Outlined.Comedy_mask,
    "批量操作" to MaterialSymbols.Outlined.Checklist,
    "首页右上角菜单" to MaterialSymbols.Outlined.Add_circle,
    "联系人详情页面" to MaterialSymbols.Outlined.Contact_page,
)

// ---------------------------------------------------------------------------
//  Root: three-tab pager + floating bottom bar, with category drill-down
// ---------------------------------------------------------------------------

@Composable
private fun SettingsRoot(onFinish: () -> Unit) {
    var openedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    var showLicense by rememberSaveable { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { 3 })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    val scope = rememberCoroutineScope()
    val backdrop = rememberLayerBackdrop()

    val barBottomPadding = 12.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // No overlay open: plain back finishes the activity.
    BackHandler(enabled = openedCategory == null && !showLicense) { onFinish() }

    val drillTarget: DrillTarget? = when {
        openedCategory != null -> DrillTarget.Category(openedCategory!!)
        showLicense -> DrillTarget.License
        else -> null
    }

    MiuixDrillDownScaffold(
        target = drillTarget,
        onClose = {
            openedCategory = null
            showLicense = false
        },
    ) {
        // Background scene: the three-tab pager + floating bar. Parallaxed + dimmed by the
        // scaffold while a drill-down is open.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MiuixTheme.colorScheme.background),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .layerBackdrop(backdrop),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    key = { it },
                ) { page ->
                    when (page) {
                        0 -> HomePager(onOpenFeatures = { scope.launch { pagerState.animateScrollToPage(1) } })
                        1 -> FeaturesPager(onOpenCategory = { openedCategory = it })
                        else -> SettingsPager(onOpenLicense = { showLicense = true })
                    }
                }
            }

            val haptic = LocalHapticFeedback.current

            FloatingBottomBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    )
                    .padding(bottom = barBottomPadding),
                selectedIndex = { pagerState.targetPage },
                // Track the pager's fractional scroll 1:1 during a finger swipe; a tab *tap*
                // (programmatic animateScrollToPage, isDragged == false) springs the pill across
                // with the press/glass bulge instead of a flat translate.
                progress = { pagerState.currentPage + pagerState.currentPageOffsetFraction },
                isTracking = { isDragged },
                onSelected = { scope.launch { pagerState.animateScrollToPage(it) } },
                backdrop = backdrop,
                tabsCount = TAB_ITEMS.size,
                isBlurEnabled = true,
                colors = FloatingBottomBarDefaults.colors(
                    containerColor = MiuixTheme.colorScheme.surfaceContainer,
                    indicatorColor = MiuixTheme.colorScheme.primary,
                    contentColor = MiuixTheme.colorScheme.onSurfaceSecondary,
                    activeContentColor = MiuixTheme.colorScheme.primary,
                ),
            ) {
                // Key the fill crossfade to targetPage (same driver as the pill), not
                // settledPage — settledPage only updates when animateScrollToPage fully
                // finishes, so the icon would fill a beat after the pill has arrived.
                val target = pagerState.targetPage
                TAB_ITEMS.forEachIndexed { index, item ->
                    FloatingBottomBarItem(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        modifier = Modifier.defaultMinSize(minWidth = 76.dp),
                    ) {
                        Crossfade(
                            targetState = index == target,
                            animationSpec = tween(200),
                            label = "navIcon",
                        ) { selected ->
                            M3Icon(
                                imageVector = if (selected) item.filled else item.outlined,
                                contentDescription = item.label,
                            )
                        }
                        M3Text(
                            text = item.label,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Visible
                        )
                    }
                }
            }
        }
    }
}

/**
 * The miuix navigation3 spring easing, ported verbatim from the miuix fork's
 * `NavTransitionEasing(response = 0.8, damping = 0.95)`. It's the analytic solution of an
 * under-damped spring expressed as an [Easing], which is what gives the Miuix predictive-back
 * transition its exact feel (vs. a plain tween or a stock Compose spring).
 */
private val NavAnimationEasing: Easing = run {
    val response = 0.8
    val damping = 0.95
    val omega = 2.0 * PI / response
    val k = omega * omega
    val c = damping * 4.0 * PI / response
    val w = sqrt(4.0 * k - c * c) / 2.0
    val r = -c / 2.0
    val c2 = r / w
    Easing { fraction ->
        val t = fraction.toDouble()
        val decay = exp(r * t)
        (decay * (-cos(w * t) + c2 * sin(w * t)) + 1.0).toFloat()
    }
}

/** Device hardware corner radius (API 31+), else a sane squircle fallback. */
@Composable
private fun deviceCornerRadiusDp(): Dp {
    val view = LocalView.current
    val density = LocalDensity.current
    return remember(view) {
        val px = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.rootWindowInsets
                ?.getRoundedCorner(android.view.RoundedCorner.POSITION_BOTTOM_LEFT)
                ?.radius ?: 0
        } else {
            0
        }
        if (px > 0) with(density) { px.toDp() } else 32.dp
    }
}

/** Which drill-down screen is showing (mutually exclusive: category detail or license). */
private sealed interface DrillTarget {
    data class Category(val name: String) : DrillTarget
    data object License : DrillTarget
}

/**
 * Faithful reproduction of miuix's navigation3 "Miuix" predictive-back transition. Unlike a
 * plain overlay slide, the *background scene* (pager + bar, in [background]) reacts too:
 *
 *  - top scene (the drill-down [DrillTarget]) slides its full width to the right on dismiss,
 *    squircle-corner-clipped at the device radius — no scale, no fade;
 *  - background scene parallaxes in from `-width/4` and is dimmed up to 50% black, both driven
 *    by the same gesture fraction;
 *  - committed open/close and gesture cancel settle on [NavAnimationEasing] (the miuix spring).
 *
 * `fraction` runs 0 (background fully covered by the top scene) → 1 (top scene fully dismissed).
 */
@Composable
private fun MiuixDrillDownScaffold(
    target: DrillTarget?,
    onClose: () -> Unit,
    background: @Composable BoxScope.() -> Unit,
) {
    var last by remember { mutableStateOf(target) }
    if (target != null) last = target

    val visible = target != null
    // 0f = top scene fully covers the background, 1f = fully dismissed to the right.
    val fraction = remember { Animatable(if (visible) 0f else 1f) }

    LaunchedEffect(visible) {
        fraction.animateTo(if (visible) 0f else 1f, animationSpec = tween(500, easing = NavAnimationEasing))
    }

    // In-app predictive back: seek `fraction` 1:1 from the gesture, commit on finish, spring
    // back to covered on cancel.
    PredictiveBackHandler(enabled = visible) { events ->
        try {
            events.collect { event -> fraction.snapTo(event.progress) }
            onClose()
        } catch (e: CancellationException) {
            fraction.animateTo(0f, animationSpec = tween(500, easing = NavAnimationEasing))
            throw e
        }
    }

    val cornerRadius = deviceCornerRadiusDp()
    val dimColor = Color.Black

    Box(modifier = Modifier.fillMaxSize()) {
        val p = fraction.value

        // Background scene: parallax + dim. It stays put once fully covered (occluded anyway).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationX = -size.width * 0.25f * (1f - p) },
        ) {
            background()
            if (p < 1f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer { alpha = 0.5f * (1f - p) }
                        .background(dimColor),
                )
            }
        }

        // Top scene: full-width slide + squircle corner clip. Rendered while open and during the
        // close animation (keeping the last value so content doesn't blank out mid-transition).
        if (visible || p < 1f) {
            val value = last
            if (value != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { translationX = size.width * p }
                        .squircleClip(cornerRadius)
                        .background(MiuixTheme.colorScheme.background)
                        // Swallow taps so the background isn't clickable through the overlay.
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        ),
                ) {
                    when (value) {
                        is DrillTarget.Category -> CategoryDetailScreen(categoryName = value.name, onBack = onClose)
                        DrillTarget.License -> LicenseScreen(onBack = onClose)
                    }
                }
            }
        }
    }
}

private data class NavItem(val label: String, val outlined: ImageVector, val filled: ImageVector)

private val TAB_ITEMS = listOf(
    NavItem("主页", MaterialSymbols.Outlined.Home, MaterialSymbols.OutlinedFilled.Home),
    NavItem("功能", MaterialSymbols.Outlined.Tune, MaterialSymbols.OutlinedFilled.Tune),
    NavItem("设置", MaterialSymbols.Outlined.Settings, MaterialSymbols.OutlinedFilled.Settings),
)

/** Bottom padding so scrollable content clears the floating bar. */
private val CONTENT_BOTTOM_INSET = 88.dp
// ---------------------------------------------------------------------------
//  Shared scaffold: miuix Scaffold + collapsing TopAppBar + scrollable column
// ---------------------------------------------------------------------------

@Composable
private fun MiuixListScaffold(
    title: String,
    navigationIcon: @Composable (() -> Unit)? = null,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit,
) {
    val scrollBehavior = MiuixScrollBehavior()
    // The LazyColumn is registered as the blur source; the top bar samples that captured
    // layer through drawBackdrop and paints itself transparent, so scrolled content shows
    // through blurred behind the collapsed bar (InstallerX's useBlur pattern, on miuix-blur glass).
    val barBackdrop = rememberLayerBackdrop()
    val barTint = MiuixTheme.colorScheme.surface.copy(alpha = 0.67f)
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.drawBackdrop(
                    backdrop = barBackdrop,
                    shape = { RectangleShape },
                    effects = {
                        vibrancy()
                        blur(24.dp.toPx(), 24.dp.toPx())
                    },
                    onDrawSurface = { drawRect(barTint) },
                ),
                // Transparent so the miuix bar's own opaque surface doesn't hide the blur.
                color = Color.Transparent,
                title = title,
                scrollBehavior = scrollBehavior,
                navigationIcon = { navigationIcon?.invoke() },
            )
        },
        popupHost = {},
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .layerBackdrop(barBackdrop)
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(horizontal = 12.dp),
            contentPadding = innerPadding,
            overscrollEffect = null,
            content = content,
        )
    }
}

// ---------------------------------------------------------------------------
//  Page 0 — Home
// ---------------------------------------------------------------------------

/**
 * Opens the LSPosed manager from within a hooked process, replicating the two-pronged shell
 * routine LSPosed itself documents:
 *  1. Start `com.android.shell/.BugreportWarningActivity` with the manager's
 *     `LAUNCH_MANAGER` category — LSPosed's hook on the shell app intercepts this and swaps in
 *     the manager UI.
 *  2. Broadcast the `*#*#5776733#*#*` SECRET_CODE (action differs on API >= 29) as a fallback
 *     for setups where the activity trick is unavailable.
 */
private fun openLsposedManager(context: Context) {
    val managerPackage = "org.lsposed.manager"
    val injectedPackage = "com.android.shell"

    runCatching {
        context.startActivity(
            Intent().apply {
                component = ComponentName(injectedPackage, "$injectedPackage.BugreportWarningActivity")
                addCategory("$managerPackage.LAUNCH_MANAGER")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }.onFailure { WeLogger.e(nameOf(SettingsActivity::class), "failed to launch LSPosed manager activity", it) }

    runCatching {
        val action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "android.telephony.action.SECRET_CODE"
        } else {
            "android.provider.Telephony.SECRET_CODE"
        }
        context.sendBroadcast(
            Intent(action, "android_secret_code://5776733".toUri()).setPackage("android")
        )
    }.onFailure { WeLogger.e(nameOf(SettingsActivity::class), "failed to broadcast LSPosed secret code", it) }
}

@Composable
private fun HomePager(onOpenFeatures: () -> Unit) {
    val enabledCount = remember {
        FeaturesProvider.ALL_HOOK_ITEMS.count { WePrefs.getBoolOrFalse(it.name) }
    }
    val totalCount = remember { FeaturesProvider.ALL_HOOK_ITEMS.size }

    MiuixListScaffold(title = "WeKit") {
        item {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatusRow(enabledCount = enabledCount, totalCount = totalCount, onOpenFeatures = onOpenFeatures)
                SystemInfoCard()
                Spacer(Modifier.height(CONTENT_BOTTOM_INSET))
            }
        }
    }
}

@Composable
private fun StatusRow(enabledCount: Int, totalCount: Int, onOpenFeatures: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: activation status. No detection — seeing this screen means the module is active.
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            colors = CardDefaults.defaultColors(
                color = when {
                    MiuixTheme.isDynamicColor -> MiuixTheme.colorScheme.secondaryContainer
                    isDark -> Color(0xFF1A3825)
                    else -> Color(0xFFDFFAE4)
                }
            ),
            showIndication = true,
            pressFeedbackType = PressFeedbackType.Tilt,
            onClick = { openLsposedManager(context) },
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(38.dp, 45.dp),
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    Icon(
                        modifier = Modifier.size(170.dp),
                        imageVector = MaterialSymbols.Outlined.Check_circle,
                        tint = if (MiuixTheme.isDynamicColor) {
                            MiuixTheme.colorScheme.primary.copy(alpha = 0.8f)
                        } else {
                            Color(0xFF36D167)
                        },
                        contentDescription = null,
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(text = "模块已激活", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text(text = BuildConfig.VERSION_NAME, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        // Right: enabled / total feature counts.
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            CountCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = "已启用功能", value = enabledCount.toString(),
                onClick = onOpenFeatures,
            )
            Spacer(Modifier.height(12.dp))
            CountCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = "全部功能", value = totalCount.toString(),
                onClick = onOpenFeatures,
            )
        }
    }
}

@Composable
private fun CountCard(modifier: Modifier, label: String, value: String, onClick: () -> Unit) {
    Card(
        modifier = modifier,
        insideMargin = PaddingValues(16.dp),
        showIndication = true,
        pressFeedbackType = PressFeedbackType.Tilt,
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text(
                text = label,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = MiuixTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun SystemInfoCard() {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            InfoText("微信版本", "${HostInfo.versionName} (${HostInfo.versionCode})")
            InfoText("模块版本", "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            InfoText("构建时间", formatEpoch(BuildConfig.BUILD_TIMESTAMP, true))
            InfoText("设备型号", "${Build.MANUFACTURER} ${Build.MODEL}")
            InfoText("Android 版本", "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})", bottomPadding = 0.dp)
        }
    }
}

@Composable
private fun InfoText(title: String, content: String, bottomPadding: Dp = 24.dp) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = MiuixTheme.colorScheme.onSurface,
    )
    Text(
        text = content,
        fontSize = 14.sp,
        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        modifier = Modifier.padding(top = 2.dp, bottom = bottomPadding),
    )
}
// ---------------------------------------------------------------------------
//  Page 1 — Features (search bar + category list)
// ---------------------------------------------------------------------------

@Composable
private fun FeaturesPager(onOpenCategory: (String) -> Unit) {
    val showAprilFools = remember { LocalDate.now().isAprilFools }

    val queryState = rememberTextFieldState()
    val query = queryState.text.toString()
    val searching = query.isNotBlank()

    val searchableItems = remember { FeaturesProvider.ALL_HOOK_ITEMS.filterIsInstance<SwitchFeature>() }
    val filteredItems = remember(query) {
        if (!searching) emptyList()
        else searchableItems.filter { it.name.contains(query, ignoreCase = true) }
    }
    val switchStates = remember { mutableStateMapOf<String, Boolean>() }

    // A back press while searching clears the query first (after the IME's own
    // back has dismissed the keyboard) rather than exiting the module settings.
    BackHandler(enabled = searching) { queryState.clearText() }

    MiuixListScaffold(title = "功能") {
        item {
            TextField(
                state = queryState,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                label = "搜索功能",
                leadingIcon = {
                    Icon(
                        imageVector = MaterialSymbols.Outlined.Search,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                },
                trailingIcon = {
                    if (searching) {
                        IconButton(onClick = { queryState.clearText() }) {
                            Icon(
                                imageVector = MaterialSymbols.Outlined.Close,
                                contentDescription = "清除",
                                tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                        }
                    }
                },
            )
        }

        if (searching) {
            // Search results replace the category list while a query is active
            if (filteredItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "未匹配到任何相关功能",
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                    ) {
                        filteredItems.forEach { item ->
                            FeatureRow(
                                item = item,
                                checked = switchStates[item.name] ?: WePrefs.getBoolOrFalse(item.name),
                                onCheckedChange = { switchStates[item.name] = it },
                            )
                        }
                    }
                }
            }
        } else {
            if (showAprilFools) {
                item {
                    Card(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                    ) {
                        ArrowPreference(
                            title = "🏳",
                            summary = "投降喵投降喵",
                            onClick = {
                                WePrefs.putBool(AprilFools.KEY_SURRENDER, true)
                                CoroutineScope(Dispatchers.Main).launch { showToastSuspend("重启生效") }
                            },
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                ) {
                    FEATURE_CATEGORIES.forEach { (name, icon) ->
                        ArrowPreference(
                            title = name,
                            startAction = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 6.dp),
                                    tint = MiuixTheme.colorScheme.onBackground,
                                )
                            },
                            onClick = { onOpenCategory(name) },
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(CONTENT_BOTTOM_INSET)) }
    }
}

// ---------------------------------------------------------------------------
//  Category detail (replaces CategorySettingsScreen)
// ---------------------------------------------------------------------------

@Composable
private fun CategoryDetailScreen(categoryName: String, onBack: () -> Unit) {
    val items = remember(categoryName) {
        FeaturesProvider.ALL_HOOK_ITEMS.filter { categoryName in it.categories }
    }
    val switchStates = remember(categoryName) {
        mutableStateMapOf<String, Boolean>().apply {
            items.forEach { put(it.name, WePrefs.getBoolOrFalse(it.name)) }
        }
    }

    MiuixListScaffold(
        title = categoryName,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = MaterialSymbols.Outlined.Arrow_back,
                    contentDescription = "返回",
                    tint = MiuixTheme.colorScheme.onBackground,
                )
            }
        },
    ) {
        if (items.isEmpty()) return@MiuixListScaffold

        item {
            Card(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) {
                items.forEach { item ->
                    FeatureRow(
                        item = item,
                        checked = switchStates[item.name] ?: false,
                        onCheckedChange = { switchStates[item.name] = it },
                    )
                    item.Ui()
                }
            }
            Spacer(Modifier.height(CONTENT_BOTTOM_INSET))
        }
    }
}

// ---------------------------------------------------------------------------
//  Open-source license screen (miuix, ported from AboutLibrariesScreen)
// ---------------------------------------------------------------------------

@Composable
private fun LicenseScreen(onBack: () -> Unit) {
    val libraries = remember {
        Libs.Builder().withJson(AboutLibrariesProvider.ABOUT_LIBRARIES_JSON).build().libraries
    }

    val queryState = rememberTextFieldState()
    val query = queryState.text.toString()
    val filtered = remember(query, libraries) {
        if (query.isBlank()) libraries
        else libraries.filter { lib ->
            lib.name.contains(query, ignoreCase = true) ||
                    lib.developers.any { it.name?.contains(query, ignoreCase = true) == true } ||
                    lib.description?.contains(query, ignoreCase = true) == true
        }
    }

    MiuixListScaffold(
        title = "开放源代码库",
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = MaterialSymbols.Outlined.Arrow_back,
                    contentDescription = "返回",
                    tint = MiuixTheme.colorScheme.onBackground,
                )
            }
        },
    ) {
        item {
            TextField(
                state = queryState,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                label = "搜索库",
                leadingIcon = {
                    Icon(
                        imageVector = MaterialSymbols.Outlined.Search,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { queryState.clearText() }) {
                            Icon(
                                imageVector = MaterialSymbols.Outlined.Close,
                                contentDescription = "清除",
                                tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                        }
                    }
                },
            )
        }

        item {
            SmallTitle(
                text = if (query.isBlank()) "${libraries.size} 个库" else "${filtered.size}/${libraries.size} 个库",
                modifier = Modifier.padding(top = 6.dp),
            )
        }

        if (filtered.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "找不到「$query」的结果",
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
            }
        } else {
            items(filtered, key = { it.uniqueId }) { library ->
                LibraryRow(library, modifier = Modifier.padding(top = 12.dp))
            }
        }

        item { Spacer(Modifier.height(CONTENT_BOTTOM_INSET)) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LibraryRow(library: Library, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = library.name,
                    fontWeight = FontWeight.SemiBold,
                    color = MiuixTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                library.artifactVersion?.let { version ->
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = version,
                        fontSize = 12.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
            }

            val author = library.developers.firstOrNull()?.name ?: library.organization?.name
            if (!author.isNullOrBlank()) {
                Text(
                    text = author,
                    fontSize = 13.sp,
                    color = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            library.description?.takeIf { it.isNotBlank() }?.let { desc ->
                Text(
                    text = desc,
                    fontSize = 13.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            if (library.licenses.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    library.licenses.forEach { license ->
                        Text(
                            text = license.name,
                            fontSize = 12.sp,
                            color = MiuixTheme.colorScheme.onSurfaceContainerVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MiuixTheme.colorScheme.surfaceContainerHigh)
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
//  Page 2 — Settings (lower half of the legacy MainSettingsScreen)
// ---------------------------------------------------------------------------

@Composable
fun SmallTitle(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MiuixTheme.colorScheme.onBackgroundVariant,
    insideMargin: PaddingValues = PaddingValues(14.dp, 8.dp)
) {
    Text(
        modifier = modifier.padding(insideMargin),
        text = text,
        style = MiuixTheme.textStyles.subtitle,
        color = textColor,
    )
}

@Composable
private fun SettingsPager(onOpenLicense: () -> Unit) {
    val context = LocalComponentActivity.current

    var showClearConfirm by remember { mutableStateOf(false) }
    var updateInfo by remember { mutableStateOf<UpdateResult.UpdateAvailable?>(null) }
    var updateError by remember { mutableStateOf<String?>(null) }

    ClearConfigDialog(show = showClearConfirm, onDismiss = { showClearConfirm = false })
    UpdateAvailableDialog(info = updateInfo, onDismiss = { updateInfo = null }, context = context)
    UpdateErrorDialog(message = updateError, onDismiss = { updateError = null })

    MiuixListScaffold(title = "设置") {
        // 界面
        item {
            SmallTitle(text = "界面", modifier = Modifier.padding(top = 12.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                ThemeSection()
            }
        }

        // 调试
        item {
            SmallTitle(text = "调试", modifier = Modifier.padding(top = 12.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                PrefSwitch(
                    key = Preferences.VERBOSE_LOG,
                    title = "详细日志",
                    summary = "输出高频日志 (这可能会暴露你的隐私信息）",
                    icon = MaterialSymbols.Outlined.Frame_bug,
                )
                PrefSwitch(
                    key = Preferences.SHOW_STARTUP_TOAST,
                    title = "显示加载完成 Toast",
                    summary = "全部功能加载完成后显示 Toast 提示",
                    icon = MaterialSymbols.Outlined.Notifications,
                )
                PrefSwitch(
                    key = Preferences.MATCH_GENERIC_WXID_EXP,
                    title = "清理消息内容微信 ID 前缀时允许非标准 ID",
                    summary = "允许处理不带 'wxid_' 前缀的微信 ID, 可能导致误伤消息原始内容 (实验性)",
                    icon = MaterialSymbols.Outlined.Rule_settings,
                )
            }
        }

        // 兼容
        item {
            SmallTitle(text = "兼容", modifier = Modifier.padding(top = 12.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                PrefSwitch(
                    key = Preferences.NO_DEX_RESOLVE,
                    title = "禁用版本适配",
                    summary = "不弹出 DEX 查找对话框，未适配功能将不会被加载",
                    icon = MaterialSymbols.Outlined.Block,
                )
                PrefArrow(
                    title = "重置适配信息",
                    summary = "清除 DEX 缓存, 等待下次启动时重新适配",
                    icon = MaterialSymbols.Outlined.Build_circle,
                    onClick = { ResetDexCache.onClick(context) },
                )
                PrefSwitch(
                    key = Preferences.RESET_DEX_ON_HOT_UPDATE,
                    title = "宿主热更新时重新适配",
                    summary = "宿主热更新时是否重置 DEX 缓存, 可能导致频繁重新适配 (实验性)",
                    icon = MaterialSymbols.Outlined.Auto_delete,
                )
            }
        }

        // 配置
        item {
            SmallTitle(text = "配置", modifier = Modifier.padding(top = 12.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                PrefArrow(
                    title = "导出配置",
                    summary = "将模块配置导出为 JSON",
                    icon = MaterialSymbols.Outlined.Upload,
                    onClick = { exportConfig(context) },
                )
                PrefArrow(
                    title = "导入配置",
                    summary = "从 JSON 导入模块配置; JSON 中的配置将会与现有配置合并, 覆盖所有已存在的配置",
                    icon = MaterialSymbols.Outlined.Download,
                    onClick = { importConfig(context) },
                )
                PrefArrow(
                    title = "清除配置",
                    summary = "清除全部模块配置 (警告: 此操作不可逆!)",
                    icon = MaterialSymbols.Outlined.Delete_forever,
                    onClick = { showClearConfirm = true },
                )
            }
        }

        // 更新
        item {
            SmallTitle(text = "更新", modifier = Modifier.padding(top = 12.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                PrefArrow(
                    title = "检查更新",
                    summary = "立即检查模块是否有新版本并自动下载",
                    icon = MaterialSymbols.Outlined.Update,
                    onClick = {
                        checkForUpdate(
                            onAvailable = { updateInfo = it },
                            onError = { updateError = it },
                        )
                    },
                )
            }
        }

        // 关于
        item {
            SmallTitle(text = "关于", modifier = Modifier.padding(top = 12.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                PrefArrow(title = "版本", summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})", icon = MaterialSymbols.Outlined.Label)
                PrefArrow(title = "构建提交时间", summary = formatEpoch(BuildConfig.BUILD_TIMESTAMP, true), icon = MaterialSymbols.Outlined.Build_circle)
                PrefArrow(
                    title = "提示",
                    summary = "牙膏要一点一点挤, 显卡要一刀一刀切, PPT 要一张一张放, 代码要一行一行写, 单个功能预计自出现在 commit 之日起, 三年内开发完毕",
                    icon = MaterialSymbols.Outlined.Lightbulb_2,
                )
                PrefArrow(
                    title = "捐赠",
                    summary = "支持项目开发 (模块完全开源免费, 捐赠无特权)",
                    icon = MaterialSymbols.Outlined.Volunteer_activism,
                    onClick = {
                        context.startActivity(Intent().apply {
                            setClassName(HostInfo.packageName, "${PackageNames.WECHAT}.plugin.collect.reward.ui.QrRewardSelectMoneyUI")
                            putExtra("key_qrcode_url", "m0n#Z7LGW*s4AVH!z'd(?)")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    },
                )
                PrefArrow(
                    title = "开放源代码许可",
                    summary = "本项目使用的开放源代码库许可",
                    icon = MaterialSymbols.Outlined.License,
                    onClick = onOpenLicense,
                )
                PrefArrow(
                    title = "GitHub",
                    summary = "Ujhhgtg/WeKit",
                    icon = GitHubIcon,
                    onClick = { "https://github.com/Ujhhgtg/WeKit".toUri().openInSystem(context, true) })
                PrefArrow(
                    title = "Telegram",
                    summary = "Telegram 超级群组",
                    icon = TelegramIcon,
                    onClick = { "https://t.me/+4XsfR-SWAtk1NGRl".toUri().openInSystem(context, true) })
            }
        }

        item { Spacer(Modifier.height(CONTENT_BOTTOM_INSET)) }
    }
}
// ---------------------------------------------------------------------------
//  界面 (theme) settings — drives ThemeSettings, which re-themes the UI live
// ---------------------------------------------------------------------------

/** A miuix dropdown bound to an enum's entries. */
@Composable
private fun <T> EnumDropdown(
    title: String,
    entries: List<T>,
    selected: T,
    labelOf: (T) -> String,
    onSelected: (T) -> Unit,
    summary: String? = null,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    WindowDropdownPreference(
        title = title,
        summary = summary,
        items = entries.map(labelOf),
        selectedIndex = entries.indexOf(selected).coerceAtLeast(0),
        enabled = enabled,
        startAction = icon?.let { { PrefIcon(it) } },
        onSelectedIndexChange = { onSelected(entries[it]) },
    )
}

@Composable
private fun ThemeSection() {
    EnumDropdown(
        title = "主题模式",
        entries = AppThemeMode.entries,
        selected = ThemeSettings.themeMode,
        labelOf = { it.displayName },
        onSelected = { ThemeSettings.updateThemeMode(it) },
        icon = MaterialSymbols.Outlined.Brightness_medium,
    )

    var customColor by remember { mutableStateOf(ThemeSettings.customColor) }
    SwitchPreference(
        title = "自定义颜色",
        summary = "使用调色板样式生成配色, 而非 Miuix 默认蓝",
        startAction = { PrefIcon(MaterialSymbols.Outlined.Palette) },
        checked = customColor,
        onCheckedChange = {
            customColor = it
            ThemeSettings.updateCustomColor(it)
        },
    )

    var showColorPicker by remember { mutableStateOf(false) }
    SeedColorPickerDialog(show = showColorPicker, onDismiss = { showColorPicker = false })

    AnimatedVisibility(visible = customColor) {
        Column {
            var dynamicWallpaper by remember { mutableStateOf(ThemeSettings.dynamicWallpaper) }
            SwitchPreference(
                title = "动态壁纸取色",
                summary = "使用系统壁纸的强调色作为种子\n需系统 Android SDK >= 31",
                startAction = { PrefIcon(MaterialSymbols.Outlined.Wallpaper) },
                checked = dynamicWallpaper,
                onCheckedChange = {
                    dynamicWallpaper = it
                    ThemeSettings.updateDynamicWallpaper(it)
                },
            )
            AnimatedVisibility(visible = !dynamicWallpaper) {
                BasicComponent(
                    title = "种子颜色",
                    summary = "点击选择配色的种子颜色",
                    startAction = { PrefIcon(MaterialSymbols.Outlined.Colorize) },
                    onClick = { showColorPicker = true },
                    endActions = {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(ThemeSettings.seedColor)),
                        )
                    },
                )
            }
            EnumDropdown(
                title = "调色板样式",
                entries = AppPaletteStyle.entries,
                selected = ThemeSettings.paletteStyle,
                labelOf = { it.displayName },
                onSelected = {
                    ThemeSettings.updatePaletteStyle(it)
                    // Keep the stored spec valid for the new style.
                    if (!it.supportsSpec2025 && ThemeSettings.colorSpec == AppColorSpec.SPEC_2025) {
                        ThemeSettings.updateColorSpec(AppColorSpec.SPEC_2021)
                    }
                },
                icon = MaterialSymbols.Outlined.Style,
            )
            val spec2025Supported = ThemeSettings.paletteStyle.supportsSpec2025
            EnumDropdown(
                title = "颜色规格",
                entries = if (spec2025Supported) AppColorSpec.entries else listOf(AppColorSpec.SPEC_2021),
                selected = ThemeSettings.effectiveColorSpec,
                labelOf = { it.displayName },
                onSelected = { ThemeSettings.updateColorSpec(it) },
                enabled = spec2025Supported,
                summary = if (!spec2025Supported) "当前调色板样式仅支持 Material 3 (2021)" else null,
                icon = MaterialSymbols.Outlined.Contrast,
            )

            var applyToWechat by remember { mutableStateOf(ThemeSettings.applyToWechat) }
            SwitchPreference(
                title = "同时对微信生效",
                summary = "将自定义配色应用到微信本身",
                startAction = { PrefIcon(MaterialSymbols.Outlined.Sync) },
                checked = applyToWechat,
                onCheckedChange = {
                    applyToWechat = it
                    ThemeSettings.updateApplyToWechat(it)
                    CoroutineScope(Dispatchers.Main).launch { showToastSuspend("重启微信生效") }
                },
            )
        }
    }
}

/** miuix color-picker dialog for the custom seed color; commits to ThemeSettings on confirm. */
@Composable
private fun SeedColorPickerDialog(show: Boolean, onDismiss: () -> Unit) {
    var picked by remember(show) { mutableStateOf(Color(ThemeSettings.seedColor)) }

    WindowDialog(show = show, title = "自定义颜色", onDismissRequest = onDismiss) {
        Column {
            ColorPicker(
                color = picked,
                onColorChanged = { picked = it },
            )
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    text = "重置",
                    onClick = { picked = Color(ThemeSettings.DEFAULT_SEED_COLOR) },
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(20.dp))
                TextButton(text = "取消", onClick = onDismiss, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(20.dp))
                TextButton(
                    text = "确定",
                    onClick = {
                        ThemeSettings.updateSeedColor(picked.toArgb())
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
//  Preference helper composables
// ---------------------------------------------------------------------------

@Composable
private fun PrefSwitch(
    key: String,
    title: String,
    summary: String,
    icon: ImageVector,
) {
    var checked by remember { mutableStateOf(WePrefs.getBoolOrFalse(key)) }
    SwitchPreference(
        title = title,
        summary = summary,
        startAction = { PrefIcon(icon) },
        checked = checked,
        onCheckedChange = {
            checked = it
            WePrefs.putBool(key, it)
        },
    )
}

@Composable
private fun PrefArrow(
    title: String,
    summary: String? = null,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
) {
    if (onClick == null) {
        // Informational row: no trailing arrow, no ripple.
        BasicComponent(
            title = title,
            summary = summary,
            startAction = icon?.let { { PrefIcon(it) } },
        )
    } else {
        ArrowPreference(
            title = title,
            summary = summary,
            startAction = icon?.let { { PrefIcon(it) } },
            onClick = onClick,
        )
    }
}

@Composable
private fun PrefIcon(icon: ImageVector) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.padding(end = 6.dp),
        tint = MiuixTheme.colorScheme.onBackground,
    )
}
// ---------------------------------------------------------------------------
//  Config import / export / clear / update / search (migrated verbatim)
// ---------------------------------------------------------------------------

private fun exportConfig(context: Context) {
    TransparentActivity.launch(context) {
        val exportLauncher = registerForActivityResult(
            ActivityResultContracts.CreateDocument("application/json")
        ) { uri ->
            if (uri == null) {
                finish()
                return@registerForActivityResult
            }
            lifecycleScope.launch(Dispatchers.IO) {
                val exportJson = run {
                    val map = WePrefs.default.getAll()
                    val jsonObject = buildJsonObject {
                        for ((key, value) in map) {
                            when (value) {
                                is Boolean -> put(key, value)
                                is Int -> put(key, value)
                                is Long -> put(key, value)
                                is Float -> put(key, value)
                                is Double -> put(key, value)
                                is String -> put(key, value)
                                is Set<*> -> put(key, buildJsonArray {
                                    @Suppress("UNCHECKED_CAST")
                                    (value as Set<String>).forEach { add(it) }
                                })

                                null -> put(key, JsonNull)
                            }
                        }
                    }
                    DefaultJson.encodeToString(jsonObject)
                }
                runCatching {
                    HostInfo.application.contentResolver.openOutputStream(uri, "w")!!.use { fos ->
                        fos.writer().use { it.write(exportJson) }
                    }
                }.onFailure {
                    showToastSuspend("导出失败!")
                    WeLogger.e("WePrefs", "failed to export", it)
                }.onSuccess { showToastSuspend("导出成功") }
                withContext(Dispatchers.Main) { finish() }
            }
        }
        exportLauncher.launch("wekit_prefs_backup.json")
    }
}

private fun importConfig(context: Context) {
    TransparentActivity.launch(context) {
        val importLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri == null) {
                finish()
                return@registerForActivityResult
            }
            lifecycleScope.launch(Dispatchers.IO) {
                runCatching {
                    val jsonString = LauncherUI.getInstance()!!.contentResolver.openInputStream(uri)?.use { fis ->
                        fis.reader().readText()
                    } ?: return@launch
                    val jsonObject = DefaultJson.parseToJsonElement(jsonString).jsonObject
                    for ((key, element) in jsonObject) {
                        when (element) {
                            is JsonNull -> WePrefs.default.remove(key)
                            is JsonPrimitive -> when {
                                element.isString -> WePrefs.default.putString(key, element.content)
                                element.booleanOrNull != null && (element.content == "true" || element.content == "false") ->
                                    WePrefs.putBool(key, element.boolean)

                                element.longOrNull != null && element.intOrNull == null ->
                                    WePrefs.putLong(key, element.long)

                                element.intOrNull != null -> WePrefs.putInt(key, element.int)
                                element.floatOrNull != null -> WePrefs.putFloat(key, element.float)
                            }

                            is JsonArray -> WePrefs.default.putStringSet(
                                key,
                                element.mapTo(HashSet()) { it.jsonPrimitive.content }
                            )

                            else -> Unit
                        }
                    }
                }.onFailure {
                    showToastSuspend("导入失败!")
                    WeLogger.e("WePrefs", "failed to import", it)
                }.onSuccess { showToastSuspend("导入成功") }
                withContext(Dispatchers.Main) { finish() }
            }
        }
        importLauncher.launch(arrayOf("application/json"))
    }
}

private fun checkForUpdate(
    onAvailable: (UpdateResult.UpdateAvailable) -> Unit,
    onError: (String) -> Unit,
) {
    CoroutineScope(Dispatchers.Main).launch {
        showToastSuspend("正在检查更新...")
        when (val result = AppUpdater.checkForUpdate()) {
            UpdateResult.UpToDate -> showToastSuspend("已是最新版本")
            is UpdateResult.UpdateAvailable -> onAvailable(result)
            is UpdateResult.Error -> {
                WeLogger.e("AppUpdater", "failed to check for updates", result.cause)
                onError(result.cause.message ?: "未知错误")
            }
        }
    }
}

// ---------------------------------------------------------------------------
//  Dialogs (miuix WindowDialog)
// ---------------------------------------------------------------------------

@Composable
private fun ClearConfigDialog(show: Boolean, onDismiss: () -> Unit) {
    MiuixConfirmDialog(
        show = show,
        title = "清除模块配置",
        message = "确定清除配置? (警告: 此操作不可逆!)",
        confirmText = "清除",
        onDismiss = onDismiss,
        onConfirm = {
            onDismiss()
            CoroutineScope(Dispatchers.IO).launch {
                showToastSuspend("正在清除...")
                WePrefs.default.clear()
                showToastSuspend("清除成功!")
            }
        },
    )
}

@Composable
private fun UpdateAvailableDialog(
    info: UpdateResult.UpdateAvailable?,
    onDismiss: () -> Unit,
    context: Context,
) {
    MiuixConfirmDialog(
        show = info != null,
        title = "检测到新版本",
        message = if (info != null) {
            "当前版本: ${BuildConfig.VERSION_NAME}\n新版本: ${info.info.versionName}\n是否下载并安装?"
        } else "",
        confirmText = "确定",
        onDismiss = onDismiss,
        onConfirm = {
            val target = info ?: return@MiuixConfirmDialog
            onDismiss()
            CoroutineScope(Dispatchers.Default).launch {
                AppUpdater.downloadAndInstall(context, target.info)
            }
        },
    )
}

@Composable
private fun UpdateErrorDialog(message: String?, onDismiss: () -> Unit) {
    MiuixMessageDialog(
        show = message != null,
        title = "检查更新失败",
        message = "错误信息: ${message.orEmpty()}",
        dismissText = "关闭",
        onDismiss = onDismiss,
    )
}

/** Two-button (cancel / confirm) miuix dialog. */
@Composable
private fun MiuixConfirmDialog(
    show: Boolean,
    title: String,
    message: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    dismissText: String = "取消",
) {
    WindowDialog(show = show, title = title, onDismissRequest = onDismiss) {
        Column {
            Text(text = message)
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(text = dismissText, onClick = onDismiss, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(20.dp))
                TextButton(
                    text = confirmText,
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                )
            }
        }
    }
}

/** Single-button (dismiss only) miuix dialog. */
@Composable
private fun MiuixMessageDialog(
    show: Boolean,
    title: String,
    message: String,
    dismissText: String,
    onDismiss: () -> Unit,
) {
    WindowDialog(show = show, title = title, onDismissRequest = onDismiss) {
        Column {
            Text(text = message)
            Spacer(Modifier.height(20.dp))
            TextButton(
                text = dismissText,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
        }
    }
}
// ---------------------------------------------------------------------------
//  Shared feature row (miuix) — used by category detail and search
// ---------------------------------------------------------------------------

@Composable
private fun FeatureRow(
    item: BaseFeature,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val context = LocalComponentActivity.current
    val configKey = item.name

    DisposableEffect(configKey) {
        (item as SwitchFeature).setToggleCompletionCallback { onCheckedChange(item.isEnabled) }
        onDispose {}
    }

    fun toggle(requested: Boolean) {
        item as SwitchFeature
        if (item.onBeforeToggle(requested, context)) {
            WePrefs.putBool(configKey, requested)
            item.isEnabled = requested
            onCheckedChange(requested)
        }
    }

    when (item) {
        is ClickableFeature -> BasicComponent(
            onClick = {
                runCatching { item.onClick(context) }
                    .onFailure { WeLogger.e(nameOf(SettingsActivity::class), "onClick failed for ${item.displayName}", it) }
            },
            endActions = {
                if (!item.noSwitchWidget) {
                    Switch(checked = checked, onCheckedChange = { toggle(it) })
                }
            },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.name,
                    fontSize = MiuixTheme.textStyles.headline1.fontSize,
                    fontWeight = FontWeight.Medium,
                    color = BasicComponentDefaults.titleColor().color,
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = MaterialSymbols.Outlined.Settings,
                    contentDescription = "Configurable",
                    modifier = Modifier
                        .padding(end = if (!item.noSwitchWidget) 8.dp else 0.dp)
                        .size(20.dp),
                    tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
            Text(
                text = item.description,
                fontSize = MiuixTheme.textStyles.body2.fontSize,
                color = BasicComponentDefaults.summaryColor().color,
            )
        }

        is SwitchFeature -> SwitchPreference(
            title = item.name,
            summary = item.description,
            checked = checked,
            onCheckedChange = { toggle(it) },
        )
    }
}








