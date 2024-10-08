package it.danielebonaldo.filamentdemo

import android.animation.ValueAnimator
import android.util.Log
import android.view.Choreographer
import android.view.MotionEvent
import android.view.Surface
import android.view.TextureView
import android.view.animation.LinearInterpolator
import com.google.android.filament.Camera
import com.google.android.filament.Engine
import com.google.android.filament.Renderer
import com.google.android.filament.SwapChain
import com.google.android.filament.View
import com.google.android.filament.Viewport
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import com.google.android.filament.gltfio.Animator
import com.google.android.filament.utils.GestureDetector
import com.google.android.filament.utils.Manipulator
import it.danielebonaldo.filamentdemo.models.Animation
import it.danielebonaldo.filamentdemo.models.ItemScene
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val kNearPlane = 0.5
private const val kFarPlane = 10000.0
private const val kFovDegrees = 45.0
private const val kAperture = 16f
private const val kShutterSpeed = 1f / 125f
private const val kSensitivity = 100f

class ModelViewer(
    val engine: Engine,
    val textureView: TextureView,
    val autoRotate: Boolean = false
) {
    val view: View = engine.createView().also {
        it.blendMode = View.BlendMode.TRANSLUCENT
    }
    val camera: Camera =
        engine.createCamera(engine.entityManager.create()).apply { setExposure(kAperture, kShutterSpeed, kSensitivity) }
    var scene: ItemScene? = null
        set(value) {
            view.scene = value?.scene
            field = value
        }

    private val uiHelper: UiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)
    private var displayHelper: DisplayHelper
    private var swapChain: SwapChain? = null
    private val renderer: Renderer = engine.createRenderer()

    private val choreographer: Choreographer
    private val frameScheduler = FrameCallback()
    private var filamentAnimator: Animator? = null
    private var animations: List<Animation> = emptyList()

    private val cameraManipulator: Manipulator
    private val gestureDetector: GestureDetector

    private val eyePos = DoubleArray(3)
    private val target = DoubleArray(3)
    private val upward = DoubleArray(3)

    private val animator = ValueAnimator.ofFloat(0.0f, (2.0 * PI).toFloat())

    init {
        view.camera = camera

        displayHelper = DisplayHelper(textureView.context)
        textureView.isOpaque = false
        uiHelper.renderCallback = SurfaceCallback()
        uiHelper.isOpaque = false
        uiHelper.attachTo(textureView)
        renderer.clearOptions = renderer.clearOptions.apply {
            clear = true
        }

        addDetachListener(textureView)

        cameraManipulator = Manipulator.Builder()
            .orbitHomePosition(4.0f, 0.5f, 4.0f)
            .viewport(textureView.width, textureView.height)
            .orbitSpeed(0.005f, 0.005f)
            .build(Manipulator.Mode.ORBIT)
        gestureDetector = GestureDetector(textureView, cameraManipulator)

        choreographer = Choreographer.getInstance()

        if (autoRotate) {
            val start = Random.nextFloat()

            animator.interpolator = LinearInterpolator()
            animator.duration = 36_000
            animator.repeatMode = ValueAnimator.RESTART
            animator.repeatCount = ValueAnimator.INFINITE
            animator.addUpdateListener { a ->
                val v = (a.animatedValue as Float) + start
                camera.lookAt(
                    cos(v) * 4.0, 0.5, sin(v) * 4.0,
                    0.0, 0.0, 0.0,
                    0.0, 1.0, 0.0
                )
            }
            animator.start()
        } else {
            camera.lookAt(
                4.0, 0.5, 4.0,
                0.0, 0.0, 0.0,
                0.0, 1.0, 0.0
            )
        }
    }

    fun onTouchEvent(event: MotionEvent) {
        if (!autoRotate) {
            gestureDetector.onTouchEvent(event)
        }
    }

    fun onTap(event: MotionEvent) {
        view.pick(
            event.x.toInt(),
            textureView.height - event.y.toInt(),
            textureView.handler,
        ) { queryResult ->
            val name = scene?.asset?.getName(queryResult.renderable) ?: return@pick
            Log.d("ModelViewer", "Tapped on $name")
        }
    }

    fun render(frameTimeNanos: Long) {
        if (!uiHelper.isReadyToRender) {
            return
        }

        if (!autoRotate) {
            cameraManipulator.getLookAt(eyePos, target, upward)
            camera.lookAt(
                eyePos[0], eyePos[1], eyePos[2],
                target[0], target[1], target[2],
                upward[0], upward[1], upward[2]
            )
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
                choreographer.postFrameCallback(frameScheduler)
            }

            override fun onViewDetachedFromWindow(v: android.view.View) {
                if (!detached) {
                    animator.cancel()

                    uiHelper.detach()

                    engine.destroyRenderer(renderer)
                    engine.destroyView(this@ModelViewer.view)
                    engine.destroyCameraComponent(camera.entity)

                    choreographer.removeFrameCallback(frameScheduler)

                    detached = true
                }
            }
        }
        view.addOnAttachStateChangeListener(AttachListener())
    }

    fun updateAnimations(filamentAnimator: Animator, animations: ImmutableList<Animation>) {
        this.filamentAnimator = filamentAnimator
        this.animations = animations
    }

    inner class SurfaceCallback : UiHelper.RendererCallback {
        override fun onNativeWindowChanged(surface: Surface) {
            swapChain?.let { engine.destroySwapChain(it) }
            swapChain = engine.createSwapChain(surface)
            displayHelper.attach(renderer, textureView.display)
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
            cameraManipulator.setViewport(width, height)
            val aspect = width.toDouble() / height.toDouble()
            camera.setProjection(kFovDegrees, aspect, kNearPlane, kFarPlane, Camera.Fov.VERTICAL)
        }
    }

    inner class FrameCallback : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            choreographer.postFrameCallback(this)

            animations.forEachIndexed { id, animation ->
                filamentAnimator?.apply {
                    val elapsedTimeSeconds = (frameTimeNanos - animation.startNano).toDouble() / 1_000_000_000
                    if (elapsedTimeSeconds <= animation.durationSeconds) {
                        applyAnimation(
                            id, when (animation.targetState) {
                                Animation.State.On -> elapsedTimeSeconds.toFloat()
                                Animation.State.Off -> animation.durationSeconds - elapsedTimeSeconds.toFloat()
                            }
                        )
                    }

                    updateBoneMatrices()
                }
            }

            render(frameTimeNanos)
        }
    }
}
