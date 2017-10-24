package com.ushan.customviewcollection.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.ushan.customviewcollection.R
import com.ushan.customviewcollection.view.CinemaSeatLayout
import com.ushan.customviewcollection.view.CinemaSeatLayout.*
import kotlinx.android.synthetic.main.activity_select_seat.*

/**
 * Created by khairil on 8/22/17.
 */
class MainActivity: AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_seat)

        cinemaSeatLayout.setAdapter(CinemaSeatAdapter())

        cinemaSeatLayout.setOnSeatSelectedListener(object : OnSeatSelectedListener {

            override fun onSelected(row: Int, column: Int, data: Any) {
                Log.i(TAG, "Selected > $row - $column - $data")
            }

            override fun onUnSelected(row: Int, column: Int, data: Any) {
                Log.i(TAG, "UnSelected > $row - $column - $data")
            }

        })
    }

    class CinemaSeatAdapter: CinemaSeatLayout.Adapter() {

        private var mLastRowTitle = 65

        override fun numberOfRows() = 14

        override fun numberOfColumn(row: Int) = 17

        override fun cinemaGuideText(row: Int): String {
            if (row == 0) mLastRowTitle = 65
            return when (row) {
                in listOf(0, 3, 4, 6, 7, 11, 12) -> ""
                else -> (mLastRowTitle++).toChar().toString()
            }
        }

        override fun componentFor(row: Int, column: Int): CinemaComponent? {
            val state = if ((row + column) % 13 == 0) {
                Seat.SEAT_STATE_UNAVAILABLE
            } else {
                Seat.SEAT_STATE_AVAILABLE
            }
            val seatText: String = when(row) {
                0 -> "BEANBAG SEAT"
                4 -> "LOUNGER SEAT"
                7 -> "CINEMA SEAT"
                12 -> "SOFA SEAT"
                else -> ""
            }
            return when {
                row in listOf(0, 4, 7, 12) -> CinemaSeatLayout.Text(row, column, seatText)
                row in listOf(1, 2) && column in listOf(0, 1, 15, 16) -> Space(row, column)
                row in listOf(3, 6, 11) -> Space(row, column)
                row == 14 && column in listOf(2, 5, 8, 11, 14) -> Space(row, column)
                else -> Seat(row, column, state, "$row - $column")
            }
        }

    }
}