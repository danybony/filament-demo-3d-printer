package it.danielebonaldo.filamentdemo

import android.view.Surface
import android.view.SurfaceView
import com.google.android.filament.Camera
import com.google.android.filament.Engine
import com.google.android.filament.Renderer
import com.google.android.filament.Scene
import com.google.android.filament.SwapChain
import com.google.android.filament.View
import com.google.android.filament.Viewport
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper

private const val kNearPlane = 0.5
private const val kFarPlane = 10000.0
private const val kFovDegrees = 45.0
private const val kAperture = 16f
private const val kShutterSpeed = 1f / 125f
private const val kSensitivity = 100f

class ModelViewer(
    val engine: Engine,
    val surfaceView: SurfaceView
) {
    val view: View = engine.createView()
    val camera: Camera =
        engine.createCamera(engine.entityManager.create()).apply { setExposure(kAperture, kShutterSpeed, kSensitivity) }
    var scene: Scene? = null
        set(value) {
            view.scene = value
            field = value
        }

    private val uiHelper: UiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)
    private var displayHelper: DisplayHelper
    private var swapChain: SwapChain? = null
    private val renderer: Renderer = engine.createRenderer()

    init {
        view.camera = camera

        displayHelper = DisplayHelper(surfaceView.context)
        uiHelper.renderCallback = SurfaceCallback()
        uiHelper.attachTo(surfaceView)
        addDetachListener(surfaceView)
        camera.lookAt(
            4.0, 0.5, 4.0,
            0.0, 0.0, 0.0,
            0.0, 1.0, 0.0
        )
    }

    fun render(frameTimeNanos: Long) {
        if (!uiHelper.isReadyToRender) {
            return
        }

        // Render the scene, unless the renderer wants to skip the frame.
        if (renderer.beginFrame(swapChain!!, frameTimeNanos)) {
            renderer.render(view)
            renderer.endFrame()
        }
    }

    private fun addDetachListener(view: android.view.View) {
        class AttachListener : android.view.View.OnAttachStateChangeListener {

            var detached = false

            override fun onViewAttachedToWindow(v: android.view.View) {
                detached = false
            }

            override fun onViewDetachedFromWindow(v: android.view.View) {
                if (!detached) {

                    uiHelper.detach()

                    engine.destroyRenderer(renderer)
                    engine.destroyView(this@ModelViewer.view)
                    engine.destroyCameraComponent(camera.entity)

                    detached = true
                }
            }
        }
        view.addOnAttachStateChangeListener(AttachListener())
    }

    inner class SurfaceCallback : UiHelper.RendererCallback {
        override fun onNativeWindowChanged(surface: Surface) {
            swapChain?.let { engine.destroySwapChain(it) }
            swapChain = engine.createSwapChain(surface)
            displayHelper.attach(renderer, surfaceView.display)
        }

        override fun onDetachedFromSurface() {
            displayHelper.detach()
            swapChain?.let {
                engine.destroySwapChain(it)
                engine.flushAndWait()
                swapChain = null
            }
        }

        override fun onResized(width: Int, height: Int) {
            view.viewport = Viewport(0, 0, width, height)
            val aspect = width.toDouble() / height.toDouble()
            camera.setProjection(kFovDegrees, aspect, kNearPlane, kFarPlane, Camera.Fov.VERTICAL)
        }
    }
}
