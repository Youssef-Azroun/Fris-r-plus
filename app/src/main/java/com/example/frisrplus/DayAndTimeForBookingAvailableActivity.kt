package com.example.frisrplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth

class DayAndTimeForBookingAvailableActivity : AppCompatActivity() {
    private var typeOfCut: String? = null
    private var price: String? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_and_time_for_booking_available)

        auth = FirebaseAuth.getInstance()

        typeOfCut = intent.getStringExtra("typeOfCut")
        price = intent.getStringExtra("price")

        val calendarView: CalendarView = findViewById(R.id.calendarView)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"
            navigateToBookingInfoActivity(selectedDate)
        }

        val buttonContainer: LinearLayout = findViewById(R.id.buttonContainer)
        for (i in 0 until buttonContainer.childCount) {
            val button: Button = buttonContainer.getChildAt(i) as Button
            button.setOnClickListener {
                navigateToBookingInfoActivity(button.text.toString())
            }
        }
    }

    private fun navigateToBookingInfoActivity(selectedTime: String) {
        val intent: Intent
        if (auth.currentUser != null) {
            // User is logged in, navigate to BookingInfoCustomerLoggedActivity
            intent = Intent(this, BookingInfoCustomerLoggedActivity::class.java)
        } else {
            // User is not logged in, navigate to BookingInfoForCostumerNoAccountsActivity
            intent = Intent(this, BookingInfoForCostumerNoAccountsActivity::class.java)
        }

        intent.putExtra("typeOfCut", typeOfCut)
        intent.putExtra("price", price)
        intent.putExtra("selectedTime", selectedTime)

        // Pass the selected date to BookingInfoCustomerLoggedActivity
        val calendarView: CalendarView = findViewById(R.id.calendarView)
        intent.putExtra("selectedDate", calendarView.date)

        startActivity(intent)
    }
}
