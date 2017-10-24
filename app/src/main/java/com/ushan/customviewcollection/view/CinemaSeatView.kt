package com.ushan.customviewcollection.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Scroller
import com.ushan.customviewcollection.R
import com.ushan.customviewcollection.view.CinemaSeatLayout.CinemaComponent
import com.ushan.customviewcollection.view.CinemaSeatLayout.Seat
import com.ushan.customviewcollection.view.CinemaSeatLayout.Text


/**
 * Created by khairil on 8/22/17.
 */
class CinemaSeatView : View {

    private val mAvailableSeatBorderPaint = Paint()
    private val mAvailableSeatFillPaint = Paint()
    private val mTakenSeatPaint = Paint()
    private val mSelectedSeatPaint = Paint()
    private val mSeatNumberPaint = Paint()
    private val mSeatTypePaint = Paint()

    private var mSeatSpacing = 16f
    private var mMaxColumnCount = 0
    private var mSeatWidth = 0f
    private var mScale = 1f
    private var mPivot = PointF(0f, 0f)
    private var mCurrentClipBounds = Rect()
    private var mCurrentRect = RectF()
    private var mOriginalRect = Rect()

    private var mScaleDetector: ScaleGestureDetector
    private var mGestureDetector: GestureDetectorCompat
    private var mScrollAnimator = ValueAnimator.ofFloat(0f, 0f)
    private var mScroller = Scroller(context)
    private var mSeats = listOf<Seat>()
    private var mComponents = listOf<CinemaComponent>()
    private var mListener: Listener? = null

    companion object {
        private const val MIN_ZOOM = 1f
        private const val MAX_ZOOM = 4f
    }

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {

        val white = ContextCompat.getColor(context, android.R.color.white)

        mScaleDetector = ScaleGestureDetector(context, mScaleGestureListener)
        mGestureDetector = GestureDetectorCompat(context, mGestureListener)
        mScroller = Scroller(context)
        mScrollAnimator.addUpdateListener {
            if (!mScroller.isFinished) {
                mScroller.computeScrollOffset()
                mPivot.set(mScroller.currX.toFloat(), mScroller.currY.toFloat())
                ViewCompat.postInvalidateOnAnimation(this@CinemaSeatView)
            } else {
                mScrollAnimator.cancel()
            }
        }

        mAvailableSeatBorderPaint.apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context, R.color.seatAvailableStroke)
            strokeWidth = 3f
        }

        mAvailableSeatFillPaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            color = white
        }

        mTakenSeatPaint.apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.seatTaken)
        }

        mSelectedSeatPaint.apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.seatSelected)
        }

        mSeatNumberPaint.apply {
            style = Paint.Style.FILL
            textSize = 20f
            color = ContextCompat.getColor(context, R.color.seatTaken)
        }

        mSeatTypePaint.apply {
            style = Paint.Style.FILL
            textSize = 25f
            color = ContextCompat.getColor(context, R.color.seatTaken)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        val clipRestoreCount = canvas.save()

        canvas.scale(mScale, mScale, mPivot.x, mPivot.y)
        drawComponents(canvas)
        canvas.getClipBounds(mCurrentClipBounds)

        canvas.restoreToCount(clipRestoreCount)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var retVal = mScaleDetector.onTouchEvent(event)
        retVal = mGestureDetector.onTouchEvent(event) || retVal
        return retVal || super.onTouchEvent(event)
    }

    private fun drawSeat(seat: Seat, canvas: Canvas, top: Float, left: Float, bottom: Float,
                         right: Float) {
        val radius = mSeatWidth * 0.35f
        val bottomRectTop = top + (mSeatWidth * 0.5f)
        val outerRect = RectF(left, top, right, bottom)
        val innerRect = RectF(left, bottomRectTop, right, bottom)
        Log.d("Rect", "$innerRect")
        when {
            seat.state == Seat.SEAT_STATE_SELECTED -> {
                canvas.drawRoundRect(outerRect, radius, radius, mSelectedSeatPaint)
                canvas.drawRect(innerRect, mSelectedSeatPaint)
            }
            seat.state == Seat.SEAT_STATE_UNAVAILABLE -> {
                canvas.drawRoundRect(outerRect, radius, radius, mTakenSeatPaint)
                canvas.drawRect(innerRect, mTakenSeatPaint)
            }
            else -> {
                val whiteRect = RectF(left + 1.5f, bottomRectTop - 1.5f,
                        right - 1.5f, bottom - 1.5f)
                val columnText = (seat.column + 1).toString()
                val textFactor = if (columnText.length == 2) 0.1f else 0.3f
                val textX = whiteRect.left + (mSeatWidth * textFactor)
                val textY = whiteRect.top + (mSeatWidth * 0.3f)
                canvas.drawRoundRect(outerRect, radius, radius, mAvailableSeatBorderPaint)
                canvas.drawRect(innerRect, mAvailableSeatBorderPaint)
                canvas.drawRect(whiteRect, mAvailableSeatFillPaint)
                canvas.drawText(columnText, textX, textY, mSeatNumberPaint)
            }
        }
    }

    private fun drawSeatTypeText(text: String, canvas: Canvas, top: Float) {
        val x = (width - mSeatTypePaint.measureText(text)) / 2
        val y = top + (mSeatWidth - (mSeatTypePaint.descent() + mSeatTypePaint.ascent())) / 2
        canvas.drawText(text, x, y, mSeatTypePaint)
    }

    private fun drawComponents(canvas: Canvas) {
        var top = 0f
        var currentRow = 0
        var left = 0f
        mComponents.forEach {
            if (it.row != currentRow) {
                currentRow = it.row
                left = 0f
                top += (mSeatWidth + mSeatSpacing)
            }
            if (it.column == 0) {
                left = mSeatSpacing
            } else {
                left += (mSeatWidth + mSeatSpacing)
            }
            val right = left + mSeatWidth
            val bottom = top + mSeatWidth

            if (it is Seat) {
                drawSeat(it, canvas, top, left, bottom, right)
            } else if (it is Text && it.column == 0) {
                drawSeatTypeText(it.text, canvas, top)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mOriginalRect = Rect(0, 0, w, h)
        val columnCount = mMaxColumnCount.toFloat()
        val totalSpacing = mSeatSpacing * columnCount
        mSeatWidth = (width - totalSpacing) / columnCount
        mListener?.onSeatWidthChanged(mSeatWidth)
        ViewCompat.postInvalidateOnAnimation(this@CinemaSeatView)
    }

    private fun onSeatClicked(row: Int, column: Int) {
        mSeats.map {
            if (it.row == row && it.column == column) {
                it.state = when {
                    it.state == Seat.SEAT_STATE_SELECTED -> Seat.SEAT_STATE_AVAILABLE
                    it.state == Seat.SEAT_STATE_AVAILABLE -> Seat.SEAT_STATE_SELECTED
                    else -> Seat.SEAT_STATE_UNAVAILABLE
                }
            }
            return@map it
        }
    }

    private fun updateScale(factor: Float) {
        mScale *= factor
        mScale = if (mScale < 1) 1f else mScale
        mScale = (mScale * 100) / 100
        mScale = maxOf(MIN_ZOOM, minOf(MAX_ZOOM, mScale))
        mCurrentRect = RectF(
                mOriginalRect.left.toFloat(),
                mOriginalRect.top.toFloat(),
                mOriginalRect.right * mScale,
                mOriginalRect.bottom * mScale
        )
        mListener?.onSeatWidthChanged(mSeatWidth * mScale)
    }

    private val mScaleGestureListener = object :
            ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mPivot.set(scaleGestureDetector.focusX, scaleGestureDetector.focusY)
            updateScale(scaleGestureDetector.scaleFactor)
            val scaledCurrentBounds = RectF(
                    mCurrentClipBounds.left * mScale,
                    mCurrentClipBounds.top * mScale,
                    mCurrentClipBounds.right * mScale,
                    mCurrentClipBounds.bottom * mScale
            )
            mListener?.onScrolled(scaledCurrentBounds.left, scaledCurrentBounds.top)
            ViewCompat.postInvalidateOnAnimation(this@CinemaSeatView)
            return true
        }
    }

    private val mGestureListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            mPivot.set(e?.x ?: 0f, e?.y ?: 0f)
            updateScale(0.5f)
            ViewCompat.postInvalidateOnAnimation(this@CinemaSeatView)
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float,
                              distanceY: Float): Boolean {
            var newX = mPivot.x + distanceX
            var newY = mPivot.y + distanceY
            newX = when {
                newX <= 0 -> 0f
                newX >= mOriginalRect.right -> mOriginalRect.right.toFloat()
                else -> newX
            }
            newY = when {
                newY <= 0 -> 0f
                newY >= mOriginalRect.bottom -> mOriginalRect.bottom.toFloat()
                else -> newY
            }

            val scaledCurrentBounds = RectF(
                    mCurrentClipBounds.left * mScale,
                    mCurrentClipBounds.top * mScale,
                    mCurrentClipBounds.right * mScale,
                    mCurrentClipBounds.bottom * mScale
            )
            mListener?.onScrolled(scaledCurrentBounds.left, scaledCurrentBounds.top)

            mPivot.set(newX, newY)
            ViewCompat.postInvalidateOnAnimation(this@CinemaSeatView)
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float,
                             velocityY: Float): Boolean {
            return false
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            val currentSeatWidth = mSeatWidth * mScale
            val currentSeatSpacing = mSeatSpacing * mScale
            val scaledCurrentBounds = RectF(
                    mCurrentClipBounds.left * mScale,
                    mCurrentClipBounds.top * mScale,
                    mCurrentClipBounds.right * mScale,
                    mCurrentClipBounds.bottom * mScale
            )
            val tappedX = ((e?.x ?: 0f) + scaledCurrentBounds.left) / (currentSeatWidth + currentSeatSpacing)
            val tappedY = ((e?.y ?: 0f) + scaledCurrentBounds.top) / (currentSeatWidth + currentSeatSpacing)
            val row = tappedY.toInt()
            val column = tappedX.toInt()

            onSeatClicked(row, column)
            ViewCompat.postInvalidateOnAnimation(this@CinemaSeatView)

            mListener?.onScrolled(scaledCurrentBounds.left, scaledCurrentBounds.top)

            return true
        }
    }

    fun addSeatViewListener(listener: Listener) {
        mListener = listener
    }

    fun populateData() {
        val rowCount = mListener?.numberOfRows() ?: 0
        val components = mutableListOf<CinemaComponent>()
        for (row in 0..rowCount) {
            val columnCount = mListener?.numberOfColumnFor(row) ?: 0
            mMaxColumnCount = maxOf(mMaxColumnCount, columnCount)
            (0..columnCount)
                    .map { mListener?.componentFor(row, it) }
                    .forEach { component ->
                        component?.let {
                            components.add(it)
                        }
                    }
        }
        mComponents = components
        ViewCompat.postInvalidateOnAnimation(this@CinemaSeatView)
    }

    interface Listener {

        fun onSeatWidthChanged(width: Float)

        fun onScrolled(x: Float, y: Float)

        fun numberOfRows(): Int

        fun numberOfColumnFor(row: Int): Int

        fun componentFor(row: Int, column: Int): CinemaComponent?

    }

}