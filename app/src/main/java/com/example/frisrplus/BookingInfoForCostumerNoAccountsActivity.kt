package com.example.frisrplus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
        val typeOfCutTextView: TextView = findViewById(R.id.textViewTypeOfCut)
        val priceTextView: TextView = findViewById(R.id.textViewPrice)
        val selectedTimeTextView: TextView = findViewById(R.id.textViewSelectedTime)
        val selectedDateTextView: TextView = findViewById(R.id.textViewSelectedDate)

        // Retrieve information from the intent
        val typeOfCut: String? = intent.getStringExtra("typeOfCut")
        val price: String? = intent.getStringExtra("price")
        val selectedTime: String? = intent.getStringExtra("selectedTime")
        val selectedDate: Long = intent.getLongExtra("selectedDate", 0L)

        // Update TextView elements with the retrieved information
        typeOfCutTextView.text = "Type: $typeOfCut"
        priceTextView.text = "Pris: $price"
        selectedTimeTextView.text = "Tid: $selectedTime"
        selectedDateTextView.text = "Datum: ${formatDate(selectedDate)}"

        // Find Button element in your layout
        val button: Button = findViewById(R.id.button)

        // Set OnClickListener for the "Button"
        button.setOnClickListener {
            // Get user input from EditText elements
            val firstName: String = firstNameEditText.text.toString()
            val lastName: String = lastNameEditText.text.toString()
            val number: String = numberEditText.text.toString()
            val email: String = emailEditText.text.toString()

            if (firstName.isEmpty() || lastName.isEmpty() || number.isEmpty() || email.isEmpty()) {
                showToast("Vänligen fyll i alla fält")
                return@setOnClickListener
            }

            // Validate number (should contain only digits)
            if (!isNumeric(number) || number.length != 10) {
                numberEditText.error = "Vänligen ange rätt telefonnummer med 10 siffor bara!"
                return@setOnClickListener
            }

            // Validate email
            if (!isValidEmail(email)) {
                emailEditText.error = "Vänligen ange rätt E-postadress"
                return@setOnClickListener
            }

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
            saveBookingToAllBookings()
            startActivity(intent)
            finish()
        }
    }
    private fun saveBookingToAllBookings() {
        val typeOfCut: String? = intent.getStringExtra("typeOfCut")
        val price: String? = intent.getStringExtra("price")
        val selectedTime: String? = intent.getStringExtra("selectedTime")
        val selectedDateMillis: Long = intent.getLongExtra("selectedDate", 0L)
        val db = Firebase.firestore

        // Convert the selectedDateMillis to a formatted date string
        val selectedDateString = formatDate(selectedDateMillis)

        // Get user input from EditText elements
        val firstName: String = findViewById<EditText>(R.id.editTextFirstName).text.toString()
        val lastName: String = findViewById<EditText>(R.id.editTextlastName).text.toString()
        val phoneNumber: String = findViewById<EditText>(R.id.editTextNummber).text.toString()
        val email: String = findViewById<EditText>(R.id.editTextEmail).text.toString()

        // Validate user input (similar to your existing validation logic)

        // User is not authenticated, proceed to save the booking data without user information
        val userBookingData = hashMapOf(
            "typeOfCut" to typeOfCut,
            "price" to price,
            "selectedTime" to selectedTime,
            "selectedDate" to selectedDateString,
            "firstName" to firstName, // Add first name
            "lastName" to lastName,   // Add last name
            "phoneNumber" to phoneNumber,   // Add phone number
            "email" to email
            // You may omit the email or add other information as needed
        )

        // Save the booking data to the AllBookings collection with the unique document ID
        db.collection("AllBookings")
            .document()
            .set(userBookingData)
            .addOnSuccessListener {
                showToast("Booking information saved to AllBookings successfully")
            }
            .addOnFailureListener { e ->
                showToast("Error saving booking information to AllBookings: ${e.message}")
            }
    }


    private fun formatDate(dateInMillis: Long): String {
        // Implement your logic to format the date as needed
        // For example, you can use SimpleDateFormat
        // For demonstration purposes, we'll use a basic format here
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(dateInMillis))
    }

    // Function to check if a string contains only digits
    private fun isNumeric(str: String): Boolean {
        return str.matches("-?\\d+".toRegex())
    }

    // Function to check if a string is a valid email address
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
