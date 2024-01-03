package com.example.frisrplus

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class CustomerAccount : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private var userBookings = listOf<UserBooking>()

    private var isAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_account)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!

        // Set up logout button
        val logOutTextView: TextView = findViewById(R.id.logOutTextView)
        logOutTextView.setOnClickListener {
            // Call the logout function
            logout()
        }

        //set up cancel button
       // val cancelButton: Button = findViewById(R.id.cancelButton)
        //cancelButton.setOnClickListener {

       // }

        // Get user information
        getUserInformation(currentUser.uid)

        // Call the function to get user bookings
        getUserBookings(currentUser.uid)
    }
    private fun getUserInformation(uid: String) {
        val db = Firebase.firestore
        val userRef = db.collection("Users").document(uid)

        userRef.addSnapshotListener { document, e ->
            if (document != null && document.exists()) {
                val user = document.toObject<User>()
                if (user != null) {
                    // Display the specific information about the logged-in user
                    val userNameTextView: TextView = findViewById(R.id.textViewMyname)
                    userNameTextView.text = "Name: ${user.firstName} ${user.lastName}\n\nE-post: ${user.email}\n\nTel: 0${user.phoneNumber}"
                }
            } else {
                Log.e("!!!", "Error fetching user data: $e")
            }
        }
    }


    private fun getUserBookings(uid: String) {
        val db = Firebase.firestore
        val bookingsRef = db.collection("UsersBookings").document(uid).collection("UserBookings")

        bookingsRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                userBookings = snapshot.documents.mapNotNull { document ->
                    val booking = document.toObject(UserBooking::class.java)
                    if (booking != null) {
                        // Set the bookingId property from the document ID
                        booking.bookingId = document.id
                        booking
                    } else {
                        null
                    }
                }

                // Update the RecyclerView with the new data
                updateRecyclerView(userBookings)
            }
        }
    }


    private fun cancelBooking(userBooking: UserBooking) {
        val uid = currentUser.uid

        val bookingId = userBooking.bookingId ?: ""
        Log.d("CancelBooking", "Canceling booking with ID: $bookingId")

        if (bookingId.isNotEmpty()) {
            // Reference to the specific booking document in Firestore
            val bookingRef = Firebase.firestore.collection("UsersBookings")
                .document(uid)
                .collection("UserBookings")
                .document(bookingId)

            val bookingRef2 = Firebase.firestore.collection("AllBookings")
                .document(bookingId)

            // Delete the document from Firestore
            bookingRef.delete()
                .addOnSuccessListener {
                    // Successfully deleted from Firestore, now update the local list
                    val updatedUserBookings = userBookings.toMutableList()
                    updatedUserBookings.remove(userBooking)
                    updateRecyclerView(updatedUserBookings)
                }
                .addOnFailureListener { e ->
                    Log.e("CancelBooking", "Error deleting document: $e")
                    // Handle the failure case if needed
                }
            bookingRef2.delete()


        } else {
            Log.e("CancelBooking", "Invalid bookingId - ${userBooking.bookingId}")
        }
    }


    private fun updateRecyclerView(userBookings: List<UserBooking>) {
        val recyclerView = findViewById<RecyclerView>(R.id.customerRecyclerView)
        val adapter = CustomerBookingRecycleAdapter(this, userBookings,
            object : ItemClickListener {
                override fun onItemClick(userBooking: UserBooking) {
                    // Implement desired behavior for button click based on user role
                    if (!isAdmin) {
                        // Admin behavior
                        showConfirmationDialog(userBooking)
                    }
                }
            },
            isAdmin
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }


    private fun logout() {
        // Log out the user using Firebase Authentication
        auth.signOut()

        // Redirect to the login activity after logout and clear the back stack
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

        // Finish the current activity to prevent the user from coming back to the logged-in state
        finish()
    }



    private fun showConfirmationDialog(userBooking: UserBooking) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Bekräfta avbokning")
        alertDialogBuilder.setMessage("Är du säkert på att du vill avboka din tid hos frisör plus?")

        alertDialogBuilder.setPositiveButton("Ja") { _, _ ->
            // User clicked "Yes", handle the cancellation
            cancelBooking(userBooking)
        }

        alertDialogBuilder.setNegativeButton("Avbryt") { dialog, _ ->
            // User clicked "Cancel", dismiss the dialog
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
