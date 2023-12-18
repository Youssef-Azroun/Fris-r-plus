package com.example.frisrplus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CalendarView

class  DayAndTimeForBookingAvailableActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_and_time_for_booking_available)

        // Find the CalendarView in your layout
        val calendarView: CalendarView = findViewById(R.id.calendarView)

        // Set a listener for date changes
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Handle the selected date
            val selectedDate = "$year-${month + 1}-$dayOfMonth"
            // Implement your logic here
        }
    }
}
