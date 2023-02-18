package moe.jeremie.owl.terminal

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min
import kotlin.math.sqrt


class JoyStickView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.JoyStickView, defStyle, 0
        )

//        _exampleString = a.getString(
//            R.styleable.JoyStickView_exampleString
//        )
//        _exampleColor = a.getColor(
//            R.styleable.JoyStickView_exampleColor,
//            exampleColor
//        )
//        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
//        // values that should fall on pixel boundaries.
//        _exampleDimension = a.getDimension(
//            R.styleable.JoyStickView_exampleDimension,
//            exampleDimension
//        )
//
//        if (a.hasValue(R.styleable.JoyStickView_exampleDrawable)) {
//            exampleDrawable = a.getDrawable(
//                R.styleable.JoyStickView_exampleDrawable
//            )
//            exampleDrawable?.callback = this
//        }

        a.recycle()

    }


    private val paint =
        Paint().apply {
            // Smooth out edges of what is drawn without affecting shape.
            isAntiAlias = true
            strokeWidth = 3.0f
            color = Color.RED
        }
    private val path = Path()

    var maxW = 100
    var maxH = 100
    var circleRadius = 50.0f
    var circleDeadAreaRadius = 5.0f
    var circlePointRadius = 10.0f

    var clickX = maxW / 2
    var clickY = maxH / 2

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        maxW = w
        maxH = h
        circleRadius = min(w, h) / 2.0f
        circlePointRadius = circleRadius / 5.0f
        circleDeadAreaRadius = circleRadius / 10.0f
        clickX = maxW / 2
        clickY = maxH / 2
    }

//    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        val viewRect = Rect()
//        getGlobalVisibleRect(viewRect)
//        if (!viewRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
//            clickX = maxW / 2
//            clickY = maxH / 2
//            // tell the View to redraw the Canvas
//            invalidate()
//        }
//        return super.dispatchTouchEvent(ev)
//    }

    public data class JoyStickState(
        val x: Int,
        val y: Int,
    )

    private var onJoyStickMove: (JoyStickState) -> Unit = {}

    public fun setOnJoyStickMove(l: (JoyStickState) -> Unit) {
        onJoyStickMove = l
    }

    public fun getJoyStickState(): JoyStickState {
        var dx = clickX - maxW / 2
        var dy = clickY - maxH / 2
        dx = dx * 1000 / (maxW / 2)
        dy = dy * 1000 / (maxH / 2)
        return JoyStickState(
            x = dx,
            y = -(dy),
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {


        val eventAction = event.action
//        if (eventAction == MotionEvent.ACTION_OUTSIDE) {
//            clickX = maxW / 2
//            clickY = maxH / 2
//            // tell the View to redraw the Canvas
//            invalidate()
//            return super.onTouchEvent(event)
//        }
        if (eventAction == MotionEvent.ACTION_UP) {
            clickX = maxW / 2
            clickY = maxH / 2
            invalidate()
            return super.onTouchEvent(event)
        }
        // you may need the x/y location
        val x = event.x.toInt()
        val y = event.y.toInt()
//        val rect = Rect()
//        rect.set(0, 0, maxW, maxH)
////        getGlobalVisibleRect(rect)
//        if (!rect.contains(x, y)) {
//            clickX = maxW / 2
//            clickY = maxH / 2
//            // tell the View to redraw the Canvas
//            invalidate()
//            return super.onTouchEvent(event)
//        }

        val distance = sqrt(
            ((maxW / 2) - event.x) * ((maxW / 2) - event.x) +
                    ((maxH / 2) - event.y) * ((maxH / 2) - event.y)
        )
        if (distance > circleRadius) {
//            clickX = maxW / 2
//            clickY = maxH / 2
            invalidate()
//            return true
            return super.onTouchEvent(event)
        }
        if (distance < circleDeadAreaRadius) {
            clickX = maxW / 2
            clickY = maxH / 2
            performClick()
            invalidate()
            onJoyStickMove(getJoyStickState())
            return true
//            return super.onTouchEvent(event)
        }

        clickX = x
        clickY = y

        performClick()
        // tell the View to redraw the Canvas
        invalidate()
        onJoyStickMove(getJoyStickState())
        // tell the View that we handled the event
        return true
//        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Save layer alpha for Rect that covers the view : alpha is 90 / 255
        canvas.saveLayerAlpha(0f, 0f, width.toFloat(), height.toFloat(), 90)

//        canvas.drawColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb())
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        setBackgroundColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb())
//        canvas.drawColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb(), PorterDuff.Mode.CLEAR)


        canvas.save()
        // https://developer.android.com/codelabs/advanced-android-kotlin-training-clipping-canvas-objects#5
        path.rewind()
        path.addCircle(circleRadius, circleRadius, circleRadius, Path.Direction.CW);
        canvas.clipPath(path)
        canvas.drawColor(Color.BLUE)

//        canvas.drawLine(0f, 0f, maxW.toFloat(), maxH.toFloat(), paint)
        canvas.drawLine(0f, maxH / 2f, maxW.toFloat(), maxH / 2f, paint)
        canvas.drawLine(maxW / 2f, 0f, maxW / 2f, maxH.toFloat(), paint)

        canvas.restore()

        canvas.save()
        path.rewind()
        path.addCircle(clickX.toFloat(), clickY.toFloat(), circlePointRadius, Path.Direction.CW)
        canvas.clipPath(path)
        canvas.drawColor(Color.YELLOW)
        canvas.restore()

    }
}