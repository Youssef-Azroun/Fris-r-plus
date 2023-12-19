package com.example.frisrplus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingInfoCustomerLoggedActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // Declare TextView variables here
    private lateinit var userEmailTextView: TextView
    private lateinit var firstNameTextView: TextView
    private lateinit var phoneNumberTextView: TextView
    private lateinit var selectedDateTextView: TextView
    private lateinit var bokaButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_info_customer_logged)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Get the information from the intent
        val typeOfCut: String? = intent.getStringExtra("typeOfCut")
        val price: String? = intent.getStringExtra("price")
        val selectedTime: String? = intent.getStringExtra("selectedTime")

        // Initialize TextView variables
        userEmailTextView = findViewById(R.id.userEmailTextView)
        firstNameTextView = findViewById(R.id.firstNameTextView)
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView)
        selectedDateTextView = findViewById(R.id.selectedDateTextView)

        // Retrieve the selected date from the intent
        val selectedDate: Long = intent.getLongExtra("selectedDate", 0L)

        // Update the selectedDateTextView with the formatted date
        selectedDateTextView.text = "Selected Date: ${formatDate(selectedDate)}"

        // Update your UI elements with the information
        val typeOfCutTextView: TextView = findViewById(R.id.typeOfCutTextView)
        val priceTextView: TextView = findViewById(R.id.priceTextView)
        val selectedTimeTextView: TextView = findViewById(R.id.selectedTimeTextView)

        typeOfCutTextView.text = typeOfCut
        priceTextView.text = price
        selectedTimeTextView.text = selectedTime

        bokaButton = findViewById(R.id.bokaButton)

        // Set OnClickListener for the "Boka" button
        bokaButton.setOnClickListener {
            // Handle the "Boka" button click
            saveBookingToFirestore()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Fetch and display user information from Firestore
        fetchAndDisplayUserInfo()
    }

    private fun formatDate(dateInMillis: Long): String {
        // Implement your logic to format the date as needed
        // For example, you can use SimpleDateFormat
        // For demonstration purposes, we'll use a basic format here
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(dateInMillis))
    }
    private fun saveBookingToFirestore() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val uid = currentUser.uid
            val typeOfCut: String? = intent.getStringExtra("typeOfCut")
            val price: String? = intent.getStringExtra("price")
            val selectedTime: String? = intent.getStringExtra("selectedTime")
            val selectedDateMillis: Long = intent.getLongExtra("selectedDate", 0L)

            // Convert the selectedDateMillis to a formatted date string
            val selectedDateString = formatDate(selectedDateMillis)

            // Fetch user information from Firestore
            firestore.collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val user = documentSnapshot.toObject(User::class.java)
                        if (user != null) {
                            // Create a data map with booking information
                            val bookingData = hashMapOf(
                                "typeOfCut" to typeOfCut,
                                "price" to price,
                                "selectedTime" to selectedTime,
                                "selectedDate" to selectedDateString, // Use the formatted date string
                                "email" to user.email,
                                "firstName" to user.firstName,
                                "lastName" to user.lastName,
                                "phoneNumber" to user.phoneNumber
                            )

                            // Save the booking data to the user-specific collection
                            firestore.collection("UsersBookings")
                                .document(uid)
                                .collection("UserBookings")
                                .add(bookingData)
                                .addOnSuccessListener {
                                    // Save the booking data to the AllBookings collection
                                    firestore.collection("AllBookings")
                                        .add(bookingData)
                                        .addOnSuccessListener {
                                            showToast("Booking information saved successfully")
                                        }
                                        .addOnFailureListener { e ->
                                            showToast("Error saving booking information to AllBookings: ${e.message}")
                                        }
                                }
                                .addOnFailureListener { e ->
                                    showToast("Error saving booking information to UserBookings: ${e.message}")
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    showToast("Error fetching user information: ${e.message}")
                }
        }
    }





    private fun fetchAndDisplayUserInfo() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val uid = currentUser.uid

            firestore.collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val user = documentSnapshot.toObject(User::class.java)
                        if (user != null) {
                            userEmailTextView.text = "Email: ${user.email}"
                            firstNameTextView.text = "Name: ${user.firstName} ${user.lastName}"
                            phoneNumberTextView.text = "Phone Number: 0${user.phoneNumber}"
                        }
                    }
                }
                .addOnFailureListener { e ->
                    showToast("Error fetching user information: ${e.message}")
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}


