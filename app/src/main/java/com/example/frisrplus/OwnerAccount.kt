package com.example.frisrplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class OwnerAccount : AppCompatActivity() {
    private var isAdmin: Boolean = true
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private val userBookings = mutableListOf<UserBooking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_account)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!

        //Sett log out
        val logOutTextView: TextView = findViewById(R.id.textViewLogOut)
        logOutTextView.setOnClickListener {
            // Call the logout function
            logoutFromOwnerActivity()
        }
        // get all bookings from firestore
        getAllBookings()
    }

    private fun getAllBookings() {
        val bookingsRef = db.collection("AllBookings")
        bookingsRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                userBookings.clear()
                //val userBookings = mutableListOf<UserBooking>()
                for (document in snapshot.documents) {
                    val booking = document.toObject<UserBooking>()
                    if (booking != null) {
                        userBookings.add(booking)
                    }
                }
                // Update the RecyclerView with the new data
                updateAllBookingRecyclerView(userBookings)
            }
        }
    }

    private fun removeItem(position: Int) {
        if (position in 0 until userBookings.size) {
            val userBookingToRemove = userBookings[position]

            // Remove the booking from Firestore
            val bookingsRef = db.collection("AllBookings")
            bookingsRef.document(userBookingToRemove.firstName.toString()).delete()
                .addOnSuccessListener {
                    // Successfully deleted from Firestore, now update the local list
                    userBookings.removeAt(position)
                    updateAllBookingRecyclerView(userBookings)
                }
        }
    }

    private fun updateAllBookingRecyclerView(userBookings: List<UserBooking>) {
        val recyclerView = findViewById<RecyclerView>(R.id.ownerRecyclerView)
        val adapter = CustomerBookingRecycleAdapter(this, userBookings,
            object : ItemClickListener {
                override fun onItemClick(position: Int) {
                    // Implement desired behavior for button click based on user role
                    if (isAdmin) {
                        // Admin behavior
                        removeItem(position)
                    }
                }
            },
            isAdmin)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun logoutFromOwnerActivity() {
        // Log out the user using Firebase Authentication
        auth.signOut()

        // Redirect to the login activity after logout and clear the back stack
        val intent = Intent(this, LogIn::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

        // Finish the current activity to prevent the user from coming back to the logged-in state
        finish()
    }

    /*private fun fetchDataFromFirestore() {
       db.collection("AllBookings")
           .get()
           .addOnSuccessListener { result ->
               val userBookings = mutableListOf<UserBooking>()

               for (document in result) {
                   val booking = document.toObject<UserBooking>()
                   userBookings.add(booking)
               }

               // Update the RecyclerView with fetched data
               updateAllBookingRecyclerView(userBookings)
           }
           .addOnFailureListener { exception ->
               Log.w("Firestore", "Error getting documents.", exception)
           }
   }*/
}
