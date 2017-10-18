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


/**
 * Created by khairil on 8/22/17.
 */
class CinemaSeatView : View {

    private val mAvailableSeatBorderPaint = Paint()
    private val mAvailableSeatFillPaint = Paint()
    private val mTakenSeatPaint = Paint()
    private val mSelectedSeatPaint = Paint()

    private var mSeatSpacing = 16f
    private var mSeatColumnCount = 20
    private var mSeatRowCount = 20
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

    companion object {
        private const val MIN_ZOOM = 1f
        private const val MAX_ZOOM = 3f
    }

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {

        val white = ContextCompat.getColor(context, android.R.color.white)

        setBackgroundColor(white)

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
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        val clipRestoreCount = canvas.save()

        canvas.scale(mScale, mScale, mPivot.x, mPivot.y)
        drawSeats(canvas)
        canvas.getClipBounds(mCurrentClipBounds)

        canvas.restoreToCount(clipRestoreCount)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var retVal = mScaleDetector.onTouchEvent(event)
        retVal = mGestureDetector.onTouchEvent(event) || retVal
        return retVal || super.onTouchEvent(event)
    }

    private fun drawSeats(canvas: Canvas) {
        var top = 0f
        var currentRow = 0
        var leading = 0f
        mSeats.forEach {
            if (it.row != currentRow) {
                currentRow = it.row
                leading = 0f
                top += (mSeatWidth + mSeatSpacing)
            }
            if (it.column == 0) {
                leading = mSeatSpacing
            } else {
                leading += (mSeatWidth + mSeatSpacing)
            }
            val right = leading + mSeatWidth
            val bottom = top + mSeatWidth
            val radius = mSeatWidth * 0.35f
            val bottomRectTop = top + (mSeatWidth * 0.5f)
            val outerRect = RectF(leading, top, right, bottom)
            val innerRect = RectF(leading, bottomRectTop, right, bottom)

            if (it.state == Seat.SEAT_STATE_SELECTED) {
                canvas.drawRoundRect(outerRect, radius, radius, mSelectedSeatPaint)
                canvas.drawRect(innerRect, mSelectedSeatPaint)
            } else if (it.state == Seat.SEAT_STATE_UNAVAILABLE) {
                canvas.drawRoundRect(outerRect, radius, radius, mTakenSeatPaint)
                canvas.drawRect(innerRect, mTakenSeatPaint)
            } else {
                val whiteRect = RectF(leading + 1.5f, bottomRectTop - 1.5f,
                        right - 1.5f, bottom - 1.5f)
                canvas.drawRoundRect(outerRect, radius, radius, mAvailableSeatBorderPaint)
                canvas.drawRect(innerRect, mAvailableSeatBorderPaint)
                canvas.drawRect(whiteRect, mAvailableSeatFillPaint)
            }
        }
//        for (row in 0..mSeatRowCount) {
//            var left = 0f
//            for (column in 0..mSeatColumnCount) {
//
//                if (column == 0) {
//                    left = mSeatSpacing
//                } else {
//                    left += (mSeatWidth + mSeatSpacing)
//                }
//
//                val right = left + mSeatWidth
//                val bottom = top + mSeatWidth
//                val radius = mSeatWidth * 0.35f
//                val bottomRectTop = top + (mSeatWidth * 0.5f)
//                val outerRect = RectF(left, top, right, bottom)
//                val innerRect = RectF(left, bottomRectTop, right, bottom)
//
//                if (isSeatSelected(row, column)) {
//                    canvas.drawRoundRect(outerRect, radius, radius, mSelectedSeatPaint)
//                    canvas.drawRect(innerRect, mSelectedSeatPaint)
//                } else {
//                    val whiteRect = RectF(left + 1.5f, bottomRectTop - 1.5f,
//                            right - 1.5f, bottom - 1.5f)
//                    canvas.drawRoundRect(outerRect, radius, radius, mAvailableSeatBorderPaint)
//                    canvas.drawRect(innerRect, mAvailableSeatBorderPaint)
//                    canvas.drawRect(whiteRect, mAvailableSeatFillPaint)
//                }
//            }
//            top += (mSeatWidth + mSeatSpacing)
//        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mOriginalRect = Rect(0, 0, w, h)
        val columnCount = mSeatColumnCount.toFloat()
        val totalSpacing = mSeatSpacing * (columnCount + 1)
        mSeatWidth = (w - totalSpacing) / columnCount
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
        log("mScale = $mScale")
    }

    private val mScaleGestureListener = object :
            ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mPivot.set(scaleGestureDetector.focusX, scaleGestureDetector.focusY)
            updateScale(scaleGestureDetector.scaleFactor)
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
            log("\n" +
                    "e1 = ${e1?.x ?: 0}, ${e1?.y ?: 0} \n " +
                    "e2 = ${e2?.x ?: 0}, ${e2?.y ?: 0} \n " +
                    "distanceX = $distanceX - distanceY = $distanceY \n " +
                    "mCurrentClipBounds = $mCurrentClipBounds \n " +
                    "mOriginalRect = ${mOriginalRect.right} \n " +
                    "mCurrentRect = ${mCurrentRect.right} \n " +
                    "e2 - e1 = ${e2!!.x - e1!!.x} \n" +
                    "mPivot = $mPivot \n"
            )
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

            log("\n" +
                    "e = ${e?.x ?: 0}, ${e?.y ?: 0} \n" +
                    "tapped = $tappedX, $tappedY \n" +
                    "mCurrentClipBounds = $mCurrentClipBounds \n" +
                    "scaledCurrentBounds = $scaledCurrentBounds \n" +
                    "mCurrentRect = ${mCurrentRect.right} \n" +
                    "currentSeatWidth = $currentSeatWidth \n" +
                    "currentSeatSpacing = ${mSeatSpacing * mScale} \n" +
                    "row = $row\n" +
                    "column = $column\n"
            )
            return true
        }
    }

    private fun log(message: String) {
        Log.d(this::class.java.simpleName, message)
    }

    fun setSeats(seats: List<Seat>) {
        mSeats = seats
        ViewCompat.postInvalidateOnAnimation(this@CinemaSeatView)
    }

}