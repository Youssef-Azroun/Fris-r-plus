package com.example.frisrplus

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
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

        // Inside the onCreate method, after setting the minimum date for the calendar view
        calendarView.minDate = Calendar.getInstance().timeInMillis

        // Fetch all booked time slots for the initial selected date and update UI
        fetchBookedTimeSlots(selectedDate, buttonContainer)

        // Add a listener to the Firestore collection to update UI when changes occur
        listenForBookingChanges(selectedDate, buttonContainer)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }

            // Reset visibility for all buttons
            for (i in 0 until buttonContainer.childCount) {
                buttonContainer.getChildAt(i).visibility = View.VISIBLE
            }

            // Check if the selected date is a Sunday
            if (isSunday(selectedDate.timeInMillis)) {
                showToast("Barber shop is closed.")

                // Hide all buttons under the calendar
                for (i in 0 until buttonContainer.childCount) {
                    buttonContainer.getChildAt(i).visibility = View.GONE
                }
            } else {
                // Update the selected date and fetch booked time slots
                this@DayAndTimeForBookingAvailableActivity.selectedDate = selectedDate.timeInMillis
                fetchBookedTimeSlots(this@DayAndTimeForBookingAvailableActivity.selectedDate, buttonContainer)

                // Check if the selected date is a Saturday
                if (isSaturday(selectedDate.timeInMillis)) {
                    // Hide the first button and the last five buttons
                    buttonContainer.getChildAt(0).visibility = View.GONE
                    for (i in buttonContainer.childCount - 5 until buttonContainer.childCount) {
                        buttonContainer.getChildAt(i).visibility = View.GONE
                    }
                }
            }
        }
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

    private fun isSunday(dateInMillis: Long): Boolean {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateInMillis
        }
        // Check if the day of the week is Sunday (Calendar.SUNDAY)
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    }

    private fun isSaturday(dateInMillis: Long): Boolean {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateInMillis
        }
        // Check if the day of the week is Saturday (Calendar.SATURDAY)
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
    }

    private fun listenForBookingChanges(selectedDate: Long, buttonContainer: LinearLayout) {
        val formattedDate = formatDate(selectedDate)

        // Create a reference to the Firestore collection for AllBookings
        val allBookingsCollection = firestore.collection("AllBookings")
            .whereEqualTo("selectedDate", formattedDate)

        // Add a snapshot listener to the AllBookings collection
        allBookingsCollection.addSnapshotListener { value, error ->
            if (error != null) {
                showToast("Error listening for booking changes: ${error.message}")
                return@addSnapshotListener
            }

            // Update UI based on changes in the AllBookings collection
            value?.let { snapshot ->
                for (i in 0 until buttonContainer.childCount) {
                    val button: Button = buttonContainer.getChildAt(i) as Button
                    val buttonTime = button.text.toString()

                    // Check if the current button time is booked
                    val isBooked = snapshot.documents.any { it["selectedTime"] == buttonTime }

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
        }
    }
}
