package com.ushan.customviewcollection.view

/**
 * Created by khairil on 10/18/17.
 */
data class Seat(val row: Int, val column: Int, var state: Int = SEAT_STATE_AVAILABLE) {
    companion object {
        const val SEAT_STATE_AVAILABLE = 0
        const val SEAT_STATE_UNAVAILABLE = 1
        const val SEAT_STATE_SELECTED = 2
    }
}