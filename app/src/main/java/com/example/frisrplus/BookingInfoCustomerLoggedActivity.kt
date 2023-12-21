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
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

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
            val currentUser = auth.currentUser
            val userEmail = currentUser?.email ?: "N/A" // Use "N/A" if email is not available

// Show the booking confirmation alert with the user's email
            showBookingConfirmationAlert(this, userEmail)
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

            // Create a unique document ID
            val bookingDocumentId = firestore.collection("UsersBookings")
                .document(uid)
                .collection("UserBookings")
                .document().id

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

                            // Save the booking data to the user-specific collection with the unique document ID
                            firestore.collection("UsersBookings")
                                .document(uid)
                                .collection("UserBookings")
                                .document(bookingDocumentId)
                                .set(bookingData)
                                .addOnSuccessListener {
                                    // Save the booking data to the AllBookings collection with the same document ID
                                    firestore.collection("AllBookings")
                                        .document(bookingDocumentId)
                                        .set(bookingData)
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

    fun showBookingConfirmationAlert(context: Context, userEmail: String) {
        val alertDialogBuilder = AlertDialog.Builder(context)

        // Set the alert message with the user's email
        val message = "Tack för din bokning hos oss Frisör plus. Du kommer inom kort få en bokning bekräftelse skickas till $userEmail."
        alertDialogBuilder.setMessage(message)

        // Set the "Okej" button and its click listener
        alertDialogBuilder.setPositiveButton("Okej") { dialogInterface: DialogInterface, _: Int ->
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            dialogInterface.dismiss()
        }

        // Create and show the alert dialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
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
                            phoneNumberTextView.text = "Telefonnummer: 0${user.phoneNumber}"
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


