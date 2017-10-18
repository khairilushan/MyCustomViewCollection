package com.ushan.customviewcollection.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ushan.customviewcollection.R
import com.ushan.customviewcollection.view.Seat
import kotlinx.android.synthetic.main.activity_select_seat.*

/**
 * Created by khairil on 8/22/17.
 */
class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_seat)
        cinemaSeatLayout.setSeats(createDummySeat())
    }

    private fun createDummySeat() : List<Seat> {
        val seats = mutableListOf<Seat>()
        for (i in 0..20) {
            for (j in 0..20) {
                val randomState = if ((i + j) % 5 == 0) Seat.SEAT_STATE_UNAVAILABLE else Seat.SEAT_STATE_AVAILABLE
                seats.add(Seat(i, j, randomState))
            }
        }
        return seats
    }

}