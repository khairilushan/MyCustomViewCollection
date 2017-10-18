package com.ushan.customviewcollection.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import com.ushan.customviewcollection.R

/**
 * Created by khairil on 10/18/17.
 */
class CinemaSeatLayout : ConstraintLayout, CinemaSeatView.SeatViewListener {

    private var mSeatView: CinemaSeatView
    private var mSeatGuideLeftView: CinemaGuideView
    private var mSeatGuideRightView: CinemaGuideView
    private var mScreenView: CinemaScreenView

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
    }

    fun setSeats(seats: List<Seat>) {
        var row = 65 //65 == A
        var currentRow = -1
        val rowLabels = mutableListOf<String>()
        seats.forEach {
            if (currentRow != it.row) {
                currentRow = it.row
                rowLabels.add(row++.toChar().toString())
            }
        }
        mSeatGuideRightView.setRowLabels(rowLabels)
        mSeatGuideLeftView.setRowLabels(rowLabels)
        mSeatView.setSeats(seats)
    }

    override fun onSeatWidthChanged(width: Float) {
        mSeatGuideLeftView.setRowHeight(width)
        mSeatGuideRightView.setRowHeight(width)
    }

    override fun onScrolled(x: Float, y: Float) {
        mSeatGuideLeftView.setVerticalScrollPosition(y)
        mSeatGuideRightView.setVerticalScrollPosition(y)
    }

}