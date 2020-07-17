package com.araratpaint.mypaint

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.araratpaint.mypaint.io.image.ImageIO
import java.util.*
import kotlin.math.abs
import kotlin.properties.Delegates


class Surface(ctx: Context) : View(ctx) {
    private var mPaint: Paint = Paint()
    var w: Int = 0
    var h: Int = 0
    private var dPath: Path = Path()
    private var paths: ArrayList<Path> = ArrayList()
    private var backupPaths: ArrayList<Path> = ArrayList()
    private var strokeWidth = 10f

    private var boundMinX = 0f
    private var boundMinY = 0f
    private var boundMaxX = 0f
    private var boundMaxY = 0f

    private var ctx: Context

    var touchState: Int by Delegates.observable(0) { _, oldValue, newValue ->
        onTouchStateChanged?.invoke(oldValue, newValue)
    }

    var onTouchStateChanged: ((Int, Int) -> Unit)? = null

    private var surfaceMatrix = Array(0) { IntArray(0) }
        get() = field

    init {
        initPaint()
        this.ctx = ctx
    }

    companion object {
        const val TOUCH_START_EVENT = 1
        const val TOUCH_MOVE_EVENT = 2
        const val TOUCH_END_EVENT = 3
    }

    private fun initPaint() {
        mPaint = Paint()
        mPaint.color = Color.BLACK
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = 10f
        mPaint.isDither = true
        mPaint.textSize = 50f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.w = w
        this.h = h
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (p in paths) {
            canvas.drawPath(p, mPaint)
        }

        canvas.drawPath(dPath, mPaint)
    }

    fun getImageAsMatrix(): Array<IntArray> {
        return surfaceMatrix
    }

    private fun touchStarted(x: Float, y: Float) {
        paths.clear()
        backupPaths.clear()

        dPath.moveTo(x, y)
        initDrawBounds(x, y)

        this.touchState = TOUCH_START_EVENT

        invalidate()
    }

    private fun touchMove(x: Float, y: Float) {
        dPath.lineTo(x, y)
        processDrawingBounds(x, y)

        this.touchState = TOUCH_MOVE_EVENT

        invalidate()
    }

    private fun touchUp() {
        paths.add(dPath)
        dPath = Path()

        processUserDrawing()

        this.touchState = TOUCH_END_EVENT
    }

    private fun initDrawBounds(startX: Float, startY: Float) {
        boundMinX = startX
        boundMinY = startY
        boundMaxX = startX
        boundMaxY = startY
    }

    private fun processDrawingBounds(currentX: Float, currentY: Float) {
        if (currentX < boundMinX) boundMinX = currentX
        if (currentY < boundMinY) boundMinY = currentY

        if (currentX > boundMaxX) boundMaxX = currentX
        if (currentY > boundMaxY) boundMaxY = currentY
    }

    fun previousStep(): Boolean {
        //no more steps
        if (paths.size <= 0) return false

        backupPaths.add(paths.removeAt(paths.size - 1))
        invalidate()

        return true
    }

    fun redoStep(): Boolean {
        //no more steps
        if (backupPaths.size <= 0) return false

        paths.add(backupPaths.removeAt(backupPaths.size - 1))
        invalidate()

        return true
    }

    fun setLineStyle() {
        mPaint.strokeWidth = this.strokeWidth
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x: Float = event!!.x
        val y: Float = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStarted(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
            }
        }

        return true
    }

    fun setBrushSize(size: Int) {
        strokeWidth = size.toFloat()

        setLineStyle()
    }

    fun processUserDrawing() {
        val x = boundMinX.toInt()
        val y = boundMinY.toInt()
        val width = abs(boundMaxX - boundMinX).toInt()
        val height = abs(boundMaxY - boundMinY).toInt()

        try {
            val img = ImageIO(this.width, this.height);
            img.draw(this)
            img.crop(x - 10, y - 10, width + 20, height + 20).scale(28, 28)
//            img.saveTo("$context.filesDir.absolutePath/$trainingNum.png")
            surfaceMatrix = img.toArray()
        } catch (e: Throwable) {
            Toast.makeText(context, "Error saving file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun clear() {
        paths.clear()
        invalidate()
    }

}