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
class FlashDealsProgressView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mOuterLinePaint = Paint()
    private val mInnerBodyPaint = Paint()
    private var mOuterRect: RectF? = null
    private var mInnerRect: RectF? = null
    private var mInnerRadius = 0f
    private val mStartColor = ContextCompat.getColor(context, R.color.progressGradientStart)
    private val mEndColor = ContextCompat.getColor(context, R.color.progressGradientEnd)
    private var mProgress: Float = 1f

    init {
        mOuterLinePaint.style = Paint.Style.STROKE
        mOuterLinePaint.strokeWidth = 2f
        mOuterLinePaint.color = ContextCompat.getColor(context, R.color.progressOuterLine)

        mInnerBodyPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        val height = canvas.height
        val radius: Float = height * 0.5f

        if (mOuterRect != null) {
            canvas.drawRoundRect(mOuterRect, radius, radius, mOuterLinePaint)
        }

        if (mInnerRect != null) {
            canvas.drawRoundRect(mInnerRect, mInnerRadius, mInnerRadius, mInnerBodyPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val outerRight = w - 1f
        val outerBottom = h - 1f

        mOuterRect = RectF(1f, 1f, outerRight, outerBottom)
        updateInnerBar()
        invalidate()
    }

    fun setProgress(progress: Float) {
        mProgress = progress
        updateInnerBar()
        invalidate()
    }

    private fun updateInnerBar() {
        if (mOuterRect != null) {
            val outerBottom = mOuterRect!!.bottom
            val outerRight = mOuterRect!!.right
            val innerHeight = outerBottom * 0.5f
            val innerY = innerHeight * 0.5f
            val innerWidth = (outerRight - innerY) * mProgress
            mInnerRadius = innerHeight * 0.5f
            mInnerRect = RectF(innerY, innerY, innerWidth, outerBottom - innerY)
            mInnerBodyPaint.shader = LinearGradient(0f, 0f, innerWidth, 0f, mStartColor,
                    mEndColor, Shader.TileMode.MIRROR)
        }
    }

}