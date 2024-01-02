package com.example.frisrplus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class OwnerAccount : AppCompatActivity() {
    private var isAdmin: Boolean = true
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_account)

        // get all bookings from firestore
        getAllBookings()
    }

    private fun getAllBookings() {
        val bookingsRef = db.collection("AllBookings")
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
                updateAllBookingRecyclerView(userBookings)
            }
        }
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

    private fun removeItem(position: Int) {
        // Implement logic to remove the item at the given position
    }

    private fun cancelBooking(position: Int) {
        // Implement cancellation logic for the item at the given position
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
                    } else {
                        // Customer behavior
                        cancelBooking(position)
                    }
                }
            },
            isAdmin)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
