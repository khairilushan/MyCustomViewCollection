package com.ushan.customviewcollection.view

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.ushan.customviewcollection.R

/**
 * Created by khairil on 8/22/17.
 */

class FlashDealsBackgroundView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mPaint = Paint()
    private var mPath = Path()
    private var mStartColor = ContextCompat.getColor(context, R.color.gradientStart)
    private var mEndColor = ContextCompat.getColor(context, R.color.gradientEnd)

    init {
        mPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        val width: Float = canvas.width.toFloat()
        val height: Float = canvas.height.toFloat()
        val startY: Float = canvas.height * 0.06f

        mPath.moveTo(width, startY)
        mPath.rQuadTo(0f, -startY, -startY, -startY)
        mPath.rLineTo(-width + (2 * startY), 0f)
        mPath.rQuadTo(-startY, 0f, -startY, startY)
        mPath.lineTo(0f, height)
        mPath.lineTo(width, height)
        mPath.close()

        canvas.drawPath(mPath, mPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPaint.shader = LinearGradient(0f, 0f, 0f, h.toFloat(), mStartColor, mEndColor,
                Shader.TileMode.MIRROR)
        invalidate()
    }

}