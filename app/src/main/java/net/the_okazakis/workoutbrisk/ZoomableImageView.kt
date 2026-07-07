package net.the_okazakis.workoutbrisk

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView


/**
 * ピンチイン・ピンチアウトでの拡大縮小、およびドラッグ移動に対応したカスタムImageView
 */
class ZoomableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var imgMatrix: Matrix = Matrix()
    private var savedMatrix: Matrix = Matrix()

    private enum class State { NONE, DRAG, ZOOM }
    private var state = State.NONE

    private var startPoint = PointF()
    private var lastPoint = PointF()

    private var minScale = 1f
    private var maxScale = 5f
    private var currentScale = 1f

    private lateinit var scaleDetector: ScaleGestureDetector
    private lateinit var gestureDetector: GestureDetector
    private var isFirstLayout = true

    init {
        scaleType = ScaleType.MATRIX
        setupGestures(context)
    }

    private fun setupGestures(context: Context) {
        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                var scaleFactor = detector.scaleFactor
                val origScale = currentScale
                currentScale *= scaleFactor

                if (currentScale > maxScale) {
                    currentScale = maxScale
                    scaleFactor = maxScale / origScale
                } else if (currentScale < minScale) {
                    currentScale = minScale
                    scaleFactor = minScale / origScale
                }

                imgMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                imageMatrix = imgMatrix
                return true
            }
        })

        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (currentScale > minScale) {
                    resetImagePosition()
                } else {
                    imgMatrix.postScale(2f, 2f, e.x, e.y)
                    currentScale = 2f
                }
                imageMatrix = imgMatrix
                return true
            }
        })
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (drawable != null && isFirstLayout) {
            resetImagePosition()
            isFirstLayout = false
        }
    }

    // 画像を画面中央に綺麗に収める処理（クラッシュ・画面外すっ飛び防止）
    private fun resetImagePosition() {
        if (drawable == null) return

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val drawableWidth = drawable.intrinsicWidth.toFloat()
        val drawableHeight = drawable.intrinsicHeight.toFloat()

        imgMatrix.reset()

        // 画面に収まるようにスケールを計算
        val scaleX = viewWidth / drawableWidth
        val scaleY = viewHeight / drawableHeight
        val scale = minOf(scaleX, scaleY)

        imgMatrix.postScale(scale, scale)

        // 中央寄せ
        val redundantXSpace = viewWidth - (scale * drawableWidth)
        val redundantYSpace = viewHeight - (scale * drawableHeight)
        imgMatrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2)

        currentScale = 1f
        imageMatrix = imgMatrix
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (drawable == null) return super.onTouchEvent(event)

        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        val currentPoint = PointF(event.x, event.y)

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(imgMatrix)
                startPoint.set(currentPoint)
                lastPoint.set(currentPoint)
                state = State.DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                state = State.ZOOM
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (state == State.DRAG) {
                    // 指が離れた瞬間に performClick() を呼び出す
                    performClick()
                }
                state = State.NONE
            }
            MotionEvent.ACTION_MOVE -> {
                if (state == State.DRAG) {
                    val dx = currentPoint.x - lastPoint.x
                    val dy = currentPoint.y - lastPoint.y
                    imgMatrix.postTranslate(dx, dy)
                    lastPoint.set(currentPoint)
                }
            }
        }

        imageMatrix = imgMatrix
        return true
    }
    override fun performClick(): Boolean {
        // スーパークラスの処理を呼び出す（これにより、セットされたOnClickListenerなどが正しく動くようになります）
        super.performClick()
        return true
    }
}