package com.example.frisrplus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirmation)

        // Retrieve data from the intent
        val firstName: String? = intent.getStringExtra("firstName")
        val lastName: String? = intent.getStringExtra("lastName")
        val number: String? = intent.getStringExtra("number")
        val email: String? = intent.getStringExtra("email")
        val typeOfCut: String? = intent.getStringExtra("typeOfCut")
        val price: String? = intent.getStringExtra("price")
        val selectedTime: String? = intent.getStringExtra("selectedTime")
        val selectedDate: Long = intent.getLongExtra("selectedDate", 0L)

        // Find TextView elements in your layout
        val firstNameTextView: TextView = findViewById(R.id.textView6)
        val lastNameTextView: TextView = findViewById(R.id.textView7)
        val numberTextView: TextView = findViewById(R.id.textView8)
        val emailTextView: TextView = findViewById(R.id.textView9)
        val typeOfCutTextView: TextView = findViewById(R.id.textView10)
        val priceTextView: TextView = findViewById(R.id.textView11)
        val selectedTimeTextView: TextView = findViewById(R.id.textView12)
        val selectedDateTextView: TextView = findViewById(R.id.textView13)

        // Update TextView elements with the retrieved information
        firstNameTextView.text = "First Name: $firstName"
        lastNameTextView.text = "Last Name: $lastName"
        numberTextView.text = "Number: $number"
        emailTextView.text = "Email: $email"
        typeOfCutTextView.text = "Type of Cut: $typeOfCut"
        priceTextView.text = "Price: $price"
        selectedTimeTextView.text = "Selected Time: $selectedTime"
        selectedDateTextView.text = "Selected Date: ${formatDate(selectedDate)}"
    }

    private fun formatDate(dateInMillis: Long): String {
        // Implement your logic to format the date as needed
        // For example, you can use SimpleDateFormat
        // For demonstration purposes, we'll use a basic format here
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(dateInMillis))
    }
}
