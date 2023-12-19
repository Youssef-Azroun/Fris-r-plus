package com.example.frisrplus

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DayAndTimeForBookingAvailableActivity : AppCompatActivity() {
    private var typeOfCut: String? = null
    private var price: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var selectedDate: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_and_time_for_booking_available)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        typeOfCut = intent.getStringExtra("typeOfCut")
        price = intent.getStringExtra("price")

        // Initialize selectedDate with the current date if not set
        if (selectedDate == 0L) {
            selectedDate = Calendar.getInstance().timeInMillis
        }

        val calendarView: CalendarView = findViewById(R.id.calendarView)
        val buttonContainer: LinearLayout = findViewById(R.id.buttonContainer)

        // Set the minimum date for the calendar view to the current date
        calendarView.minDate = Calendar.getInstance().timeInMillis

        for (i in 0 until buttonContainer.childCount) {
            val button: Button = buttonContainer.getChildAt(i) as Button
            button.setOnClickListener {
                val selectedTime = button.text.toString()

                // Check if the selected date and time are available
                checkAvailability(selectedTime, selectedDate, button)
            }
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Store the selected date when the user interacts with the calendar
            selectedDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }.timeInMillis

            // Fetch all booked time slots for the selected date and update UI
            fetchBookedTimeSlots(selectedDate, buttonContainer)
        }

        // Fetch all booked time slots for the initial selected date and update UI
        fetchBookedTimeSlots(selectedDate, buttonContainer)
    }

    private fun fetchBookedTimeSlots(selectedDate: Long, buttonContainer: LinearLayout) {
        val formattedDate = formatDate(selectedDate)

        // Query Firestore to get all booked time slots for the selected date from AllBookings collection
        firestore.collection("AllBookings")
            .whereEqualTo("selectedDate", formattedDate)
            .get()
            .addOnSuccessListener { documents ->
                for (i in 0 until buttonContainer.childCount) {
                    val button: Button = buttonContainer.getChildAt(i) as Button
                    val buttonTime = button.text.toString()

                    // Check if the current button time is booked
                    val isBooked = documents.any { it["selectedTime"] == buttonTime }

                    if (isBooked) {
                        // The time is booked, disable button and change color
                        button.isEnabled = false
                        button.setBackgroundColor(Color.RED)
                    } else {
                        // The time is not booked, enable button and reset color
                        button.isEnabled = true
                        button.setBackgroundColor(Color.BLACK) // Set to your original color
                    }
                }
            }
            .addOnFailureListener { e ->
                showToast("Error fetching booked time slots: ${e.message}")
            }
    }

    private fun checkAvailability(selectedTime: String, selectedDate: Long, button: Button) {
        val formattedDate = formatDate(selectedDate)

        // Query Firestore to check availability from AllBookings collection
        firestore.collection("AllBookings")
            .whereEqualTo("selectedDate", formattedDate)
            .whereEqualTo("selectedTime", selectedTime)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // The selected time is available
                    navigateToBookingInfoActivity(selectedTime, selectedDate)
                } else {
                    // The selected time is not available, disable button and change color
                    button.isEnabled = false
                    button.setBackgroundColor(Color.RED)
                    showToast("Selected time is already booked.")
                }
            }
            .addOnFailureListener { e ->
                showToast("Error checking availability: ${e.message}")
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

    private fun formatDate(dateInMillis: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(dateInMillis))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
