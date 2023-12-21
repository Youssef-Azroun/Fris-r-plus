package com.example.frisrplus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingInfoForCostumerNoAccountsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_info_for_costumer_no_accounts)

        // Find EditText elements in your layout
        val firstNameEditText: EditText = findViewById(R.id.editTextFirstName)
        val lastNameEditText: EditText = findViewById(R.id.editTextlastName)
        val numberEditText: EditText = findViewById(R.id.editTextNummber)
        val emailEditText: EditText = findViewById(R.id.editTextEmail)


        // Find TextView elements in your layout
        val typeOfCutTextView: TextView = findViewById(R.id.textView3)
        val priceTextView: TextView = findViewById(R.id.textView4)
        val selectedTimeTextView: TextView = findViewById(R.id.textView5)
        val selectedDateTextView: TextView = findViewById(R.id.textView14)

        // Retrieve information from the intent
        val typeOfCut: String? = intent.getStringExtra("typeOfCut")
        val price: String? = intent.getStringExtra("price")
        val selectedTime: String? = intent.getStringExtra("selectedTime")
        val selectedDate: Long = intent.getLongExtra("selectedDate", 0L)

        // Update TextView elements with the retrieved information
        typeOfCutTextView.text = "Type of Cut: $typeOfCut"
        priceTextView.text = "Price: $price"
        selectedTimeTextView.text = "Selected Time: $selectedTime"
        selectedDateTextView.text = "Selected Date: ${formatDate(selectedDate)}"

        // Find Button element in your layout
        val button: Button = findViewById(R.id.button)

        // Set OnClickListener for the "Button"
        button.setOnClickListener {
            // Get user input from EditText elements
            val firstName: String = firstNameEditText.text.toString()
            val lastName: String = lastNameEditText.text.toString()
            val number: String = numberEditText.text.toString()
            val email: String = emailEditText.text.toString()

            // Create an Intent to start BookingConfirmationActivity
            val intent = Intent(this, BookingConfirmationActivity::class.java)

            // Put data as extras in the Intent
            intent.putExtra("firstName", firstName)
            intent.putExtra("lastName", lastName)
            intent.putExtra("number", number)
            intent.putExtra("email", email)
            intent.putExtra("typeOfCut", typeOfCut)
            intent.putExtra("price", price)
            intent.putExtra("selectedTime", selectedTime)
            intent.putExtra("selectedDate", selectedDate)  // Include the selected date

            // Start BookingConfirmationActivity with the Intent
            startActivity(intent)
        }
    }
    private fun formatDate(dateInMillis: Long): String {
        // Implement your logic to format the date as needed
        // For example, you can use SimpleDateFormat
        // For demonstration purposes, we'll use a basic format here
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(dateInMillis))
    }
}
