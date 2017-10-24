package com.ushan.customviewcollection.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import com.ushan.customviewcollection.R

/**
 * Created by khairil on 10/18/17.
 */
class CinemaSeatLayout : ConstraintLayout, CinemaSeatView.Listener, CinemaGuideView.Listener {

    private var mSeatView: CinemaSeatView
    private var mSeatGuideLeftView: CinemaGuideView
    private var mSeatGuideRightView: CinemaGuideView
    private var mScreenView: CinemaScreenView
    private var mAdapter: Adapter? = null

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {

        val view = LayoutInflater.from(context).inflate(R.layout.layout_cinema_seat, this, true)

        mSeatView = view.findViewById(R.id.cinemaSeatView)
        mSeatGuideLeftView = view.findViewById(R.id.cinemaGuideViewLeft)
        mSeatGuideRightView = view.findViewById(R.id.cinemaGuideViewRight)
        mScreenView = view.findViewById(R.id.cinemaScreenView)

        mSeatView.addSeatViewListener(this)
        mSeatGuideLeftView.addGuideViewListener(this)
        mSeatGuideRightView.addGuideViewListener(this)
    }

    fun setAdapter(adapter: Adapter) {
        mAdapter = adapter
        mSeatView.populateData()

        val rowCount = mAdapter?.numberOfRows() ?: 0
        mSeatGuideRightView.populateData(rowCount)
        mSeatGuideLeftView.populateData(mAdapter?.numberOfRows() ?: 0)
    }

    override fun onSeatWidthChanged(width: Float) {
        mSeatGuideLeftView.setRowHeight(width)
        mSeatGuideRightView.setRowHeight(width)
    }

    override fun onScrolled(x: Float, y: Float) {
        mSeatGuideLeftView.setVerticalScrollPosition(y)
        mSeatGuideRightView.setVerticalScrollPosition(y)
    }

    override fun numberOfRows() = mAdapter?.numberOfRows() ?: 0

    override fun numberOfColumnFor(row: Int) = mAdapter?.numberOfColumn(row) ?: 0

    override fun componentFor(row: Int, column: Int): CinemaComponent? {
        return mAdapter?.componentFor(row, column)
    }

    override fun guideTextFor(row: Int) = mAdapter?.cinemaGuideText(row) ?: ""

    abstract class Adapter {

        abstract fun numberOfRows(): Int

        abstract fun numberOfColumn(row: Int): Int

        abstract fun cinemaGuideText(row: Int): String

        abstract fun componentFor(row: Int, column: Int): CinemaComponent?

    }

    abstract class CinemaComponent {

        abstract val row: Int

        abstract val column: Int

    }

    class Space(override val row: Int, override val column: Int) : CinemaComponent()

    class Text(override val row: Int, override val column: Int, val text: String) : CinemaComponent()

    class Seat(override val row: Int,
               override val column: Int,
               var state: Int = SEAT_STATE_AVAILABLE,
               var data: Any) :
            CinemaComponent() {

        companion object {
            const val SEAT_STATE_AVAILABLE = 0
            const val SEAT_STATE_UNAVAILABLE = 1
            const val SEAT_STATE_SELECTED = 2
        }

    }

}