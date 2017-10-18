package com.ushan.customviewcollection.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import com.ushan.customviewcollection.R

/**
 * Created by khairil on 10/18/17.
 */
class CinemaScreenView : View {

    private var mScreenPaint = Paint()
    private var mScreenLabelPaint = Paint()
    private var mScreenPath = Path()

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {

        mScreenPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = 7f
            color = ContextCompat.getColor(context, R.color.seatTaken)
        }

        mScreenLabelPaint.apply {
            style = Paint.Style.FILL
            textSize = 30f
            color = ContextCompat.getColor(context, R.color.seatTaken)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(mScreenPath, mScreenPaint)
        val labelX = width * 0.47f
        val labelY = height * 0.8f
        canvas?.drawText("SCREEN", labelX, labelY, mScreenLabelPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val screenWidth = w * 0.8f
        val halfWidth = w * 0.5f
        val halfScreenWidth = screenWidth * 0.5f
        val halfHeight = h * 0.5f
        val left = halfWidth - halfScreenWidth
        val startTop = halfHeight + (h * 0.3f)
        val curveY = halfHeight - halfHeight
        val curveX = screenWidth * 0.55f
        val right = halfWidth + halfScreenWidth
        mScreenPath.moveTo(left, startTop)
        mScreenPath.cubicTo(left, startTop, curveX, curveY, right, startTop)
        ViewCompat.postInvalidateOnAnimation(this)
    }
}