package com.example.frisrplus

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
        val cancelButton: Button = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {

        }

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
                val userBookings = mutableListOf<UserBooking>()
                for (document in snapshot.documents) {
                    val booking = document.toObject<UserBooking>()
                    if (booking != null) {
                        userBookings.add(booking)
                    }
                }
                // Update the RecyclerView with the new data
                updateRecyclerView(userBookings)
            }
        }
    }

    private fun updateRecyclerView(userBookings: List<UserBooking>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = CustomerBookingRecycleAdapter(this, userBookings)
        recyclerView.adapter = adapter
        // You may also want to set a layout manager, e.g., LinearLayoutManager
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
}
