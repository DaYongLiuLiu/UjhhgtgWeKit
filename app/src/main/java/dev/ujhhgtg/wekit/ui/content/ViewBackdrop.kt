package dev.ujhhgtg.wekit.ui.content

import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import top.yukonga.miuix.kmp.blur.Backdrop

/**
 * A [Backdrop] that samples an arbitrary native Android [View] — not the Compose tree.
 *
 * The miuix-blur backdrop effects (blur / lens / vibrancy) apply a `RenderEffect` to a
 * [GraphicsLayer] whose contents must be recorded *from within Compose*. The built-in
 * `LayerBackdrop` therefore only ever contains Compose-drawn pixels, which is useless when the
 * glass floats as an overlay on top of WeChat's own native views: there is nothing Compose-drawn
 * behind it, so the layer is empty and the glass shows nothing.
 *
 * This backdrop instead records `sourceView` (WeChat's ViewPager) into the layer whenever the
 * source content redraws, so the real chat / contacts / discover content shows through the glass.
 * It cannot capture hardware surfaces (SurfaceView / TextureView — e.g. video calls or Channels),
 * which draw blank behind the bar; that is an accepted limitation of View.draw().
 */
@Composable
fun rememberViewBackdrop(sourceView: View): ViewBackdrop {
    val graphicsLayer = rememberGraphicsLayer()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val backdrop = remember(graphicsLayer) { ViewBackdrop(graphicsLayer) }
    backdrop.sourceView = sourceView
    backdrop.density = density
    backdrop.layoutDirection = layoutDirection

    // Ask the glass to re-capture whenever WeChat's own content is about to redraw — a scroll, a
    // tab switch (setCurrentItem scrolls the pager), an incoming message, etc. `bumpVersion` writes
    // a snapshot state that `drawBackdrop` reads, which is what actually forces the backdrop draw node
    // to re-run its layer recording; a plain View.invalidate() only recomposites the cached render
    // nodes and would NOT re-run the capture, so a settled static tab froze on its last frame.
    //
    // The `isDirty` gate is what keeps this from looping at 60fps: our own re-capture dirties the
    // overlay ComposeView, not `sourceView`, so `sourceView.isDirty` is true only when WeChat's
    // content genuinely needs to redraw. When WeChat is idle nothing dirties the source, we skip the
    // bump, and the glass costs nothing.
    DisposableEffect(sourceView) {
        val observer = sourceView.viewTreeObserver
        val listener = ViewTreeObserver.OnPreDrawListener {
            if (sourceView.isDirty) {
                backdrop.bumpVersion()
            }
            true
        }
        observer.addOnPreDrawListener(listener)
        onDispose {
            (if (observer.isAlive) observer else sourceView.viewTreeObserver)
                .removeOnPreDrawListener(listener)
        }
    }

    return backdrop
}

@Stable
class ViewBackdrop internal constructor(
    private val graphicsLayer: GraphicsLayer
) : Backdrop {

    internal var sourceView: View? = null
    internal var density: Density = Density(1f)
    internal var layoutDirection: LayoutDirection = LayoutDirection.Ltr

    // Bumped whenever the source content redraws. Read inside drawBackdrop so the draw phase
    // subscribes to it: a change re-runs the backdrop draw node's layer recording (and thus our
    // recapture) rather than just recompositing stale render nodes.
    private var version by mutableIntStateOf(0)

    // Coordinate lookups are done against the source view's window position, so the offset the
    // effect needs doesn't depend on Compose recomposition.
    override val isCoordinatesDependent: Boolean = true

    // We always record at full resolution and never downscale, so no sub-pixel residual to report.
    override val offsetResidualX: Float = 0f
    override val offsetResidualY: Float = 0f

    internal fun bumpVersion() {
        version++
    }

    /**
     * Records the current pixels of [sourceView] into the backing layer. Returns true if a capture
     * was performed (so the caller can repaint), false if the view isn't ready yet.
     */
    internal fun recordSource(): Boolean {
        val view = sourceView ?: return false
        val width = view.width
        val height = view.height
        if (width <= 0 || height <= 0) return false

        graphicsLayer.record(density, layoutDirection, IntSize(width, height)) {
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas
                // Replicate the scroll translation the parent normally applies when it draws this
                // view. WeChat's WxViewPager selects the visible tab by scrolling itself
                // (scrollTo(clientWidth * position, 0)) — pages are laid out side by side and only
                // the pager's own scrollX chooses which one shows. That scroll offset is applied by
                // the *parent's* draw traversal, NOT by the public View.draw(canvas) we call here, so
                // without this translate every page draws at its raw layout x and only page 0 (home)
                // fits in the layer. This is why the glass was stuck on the home tab.
                val checkpoint = nativeCanvas.save()
                nativeCanvas.translate(-view.scrollX.toFloat(), -view.scrollY.toFloat())
                view.draw(nativeCanvas)
                nativeCanvas.restoreToCount(checkpoint)
            }
        }
        return true
    }

    override fun DrawScope.drawBackdrop(
        density: Density,
        coordinates: LayoutCoordinates?,
        layerBlock: (GraphicsLayerScope.() -> Unit)?,
        // We record the native view at full resolution and ignore downscaling — miuix passes
        // downscaleFactor > 1 as a perf hint the LayerBackdrop honors, but our native-View capture
        // always draws full-res, matching the previous behavior.
        downscaleFactor: Int,
    ) {
        @Suppress("UNUSED_EXPRESSION") version // subscribe the draw phase to source redraws
        val view = sourceView ?: return
        val barCoordinates = coordinates ?: return

        // Capture the source here, during the overlay's own draw. viewParent draws the source
        // (mViewPager) before this ComposeView, so its display list is already the current frame —
        // unlike an OnPreDrawListener, which would capture the previous frame and freeze the glass
        // on a settled static tab. If the source isn't ready yet, keep whatever was last recorded.
        recordSource()

        // Offset so the region of the source view directly behind the bar lines up with the bar.
        val barInWindow = barCoordinates.positionInWindow()
        val viewLocation = IntArray(2).also { view.getLocationInWindow(it) }
        val dx = viewLocation[0] - barInWindow.x
        val dy = viewLocation[1] - barInWindow.y
        translate(dx, dy) {
            drawLayer(graphicsLayer)
        }
    }
}
