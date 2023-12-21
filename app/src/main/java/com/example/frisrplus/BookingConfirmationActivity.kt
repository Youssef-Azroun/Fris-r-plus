package com.example.frisrplus

import android.content.Intent
import android.os.Bundle
import android.view.View
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
        val firstNameTextView: TextView = findViewById(R.id.textViewFirstName)
        val numberTextView: TextView = findViewById(R.id.textViewNumber)
        val emailTextView: TextView = findViewById(R.id.textViewEmail)
        val typeOfCutTextView: TextView = findViewById(R.id.textViewType)
        val priceTextView: TextView = findViewById(R.id.textViewForPrice)
        val selectedTimeTextView: TextView = findViewById(R.id.textViewTime)
        val selectedDateTextView: TextView = findViewById(R.id.textViewDay)

        // Update TextView elements with the retrieved information
        firstNameTextView.text = "Name: ${firstName} ${lastName}"
        numberTextView.text = "Telefonnummer: $number"
        emailTextView.text = "E-post: $email"
        typeOfCutTextView.text = "Type: $typeOfCut"
        priceTextView.text = "Pris: $price"
        selectedTimeTextView.text = "Tid: $selectedTime"
        selectedDateTextView.text = "Datum: ${formatDate(selectedDate)}"
    }

    private fun formatDate(dateInMillis: Long): String {
        // Implement your logic to format the date as needed
        // For example, you can use SimpleDateFormat
        // For demonstration purposes, we'll use a basic format here
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(dateInMillis))
    }

    fun goToFirstMainActivity(view : View){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
