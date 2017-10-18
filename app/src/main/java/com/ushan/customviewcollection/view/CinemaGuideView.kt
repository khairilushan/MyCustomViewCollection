package com.ushan.customviewcollection.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.ushan.customviewcollection.R

/**
 * Created by khairil on 10/18/17.
 */
class CinemaGuideView : View {

    private var mRows = listOf<String>()
    private var mLabelPaint = Paint()
    private var mLabelSpacing = 16f
    private var mRowHeight = 0f
    private var mVerticalScroll = 0f

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {

        mLabelPaint.apply {
            style = Paint.Style.FILL
            textSize = 20f
            color = ContextCompat.getColor(context, R.color.seatTaken)
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        val clipRestoreCount = canvas.save()
        canvas.translate(0f, mVerticalScroll)
        drawRowLabels(canvas)
        canvas.restoreToCount(clipRestoreCount)
    }

    private fun drawRowLabels(canvas: Canvas) {
        var y = 0f
        val x = canvas.width * 0.4f
        mRows.forEachIndexed { index, row -> Unit
            if (index == 0) {
                y = 0f
            } else {
                y += (mRowHeight + mLabelSpacing)
            }
            val labelY = y + mLabelSpacing
            Log.d(this@CinemaGuideView::class.java.simpleName, "" +
                    "y = $y\n" +
                    "labelY = $labelY\n" +
                    "mRowHeight = $mRowHeight\n" +
                    "mLabelSpacing = $mLabelSpacing"
            )
            canvas.drawText(row, x, labelY, mLabelPaint)
        }
    }

    fun setRowLabels(rows: List<String>) {
        mRows = rows
        ViewCompat.postInvalidateOnAnimation(this)
    }

    fun setRowHeight(height: Float) {
        val scale = height / mRowHeight
        val newTextSize = mLabelPaint.textSize * scale
        if (height != mRowHeight && mRowHeight != 0f && newTextSize <= 40f && newTextSize >= 20f) {
            mLabelPaint.textSize = newTextSize
        }
        if (mRowHeight != 0f) {
            mLabelSpacing *= scale
        }
        mRowHeight = height
        ViewCompat.postInvalidateOnAnimation(this)
    }

    fun setVerticalScrollPosition(y: Float) {
        mVerticalScroll = -y
        ViewCompat.postInvalidateOnAnimation(this)
    }

}