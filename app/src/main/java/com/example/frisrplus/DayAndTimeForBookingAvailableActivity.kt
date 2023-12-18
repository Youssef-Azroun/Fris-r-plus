package com.example.frisrplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class DayAndTimeForBookingAvailableActivity : AppCompatActivity() {
    private var typeOfCut: String? = null
    private var price: String? = null
    private lateinit var auth: FirebaseAuth
    private var selectedDate: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_and_time_for_booking_available)

        auth = FirebaseAuth.getInstance()

        typeOfCut = intent.getStringExtra("typeOfCut")
        price = intent.getStringExtra("price")

        val calendarView: CalendarView = findViewById(R.id.calendarView)

        val buttonContainer: LinearLayout = findViewById(R.id.buttonContainer)
        for (i in 0 until buttonContainer.childCount) {
            val button: Button = buttonContainer.getChildAt(i) as Button
            button.setOnClickListener {
                // Use the current date selected in the calendar
                navigateToBookingInfoActivity(button.text.toString(), selectedDate)
            }
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Store the selected date when the user interacts with the calendar
            selectedDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }.timeInMillis
        }
    }

    private fun navigateToBookingInfoActivity(selectedTime: String, selectedDate: Long) {
        val intent: Intent
        if (auth.currentUser != null) {
            intent = Intent(this, BookingInfoCustomerLoggedActivity::class.java)
        } else {
            intent = Intent(this, BookingInfoForCostumerNoAccountsActivity::class.java)
        }

        intent.putExtra("typeOfCut", typeOfCut)
        intent.putExtra("price", price)
        intent.putExtra("selectedTime", selectedTime)
        intent.putExtra("selectedDate", selectedDate)

        startActivity(intent)
    }
}
