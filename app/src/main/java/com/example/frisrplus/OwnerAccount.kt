package com.example.frisrplus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class OwnerAccount : AppCompatActivity() {
    private var isAdmin: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_account)



        getAllBookings()
    }


    private fun getAllBookings() {
        val db = Firebase.firestore
        val allBookingRef = db.collection("AllBookings")

        allBookingRef.addSnapshotListener { snapshot, e ->
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

    private fun removeItem(position: Int) {
        // Implementera logik för att ta bort objektet på den givna positionen från listan
    }

    private fun cancelBooking(position: Int) {
        // Implementera avbokningslogik för objektet på den givna positionen
    }

    private fun updateAllBookingRecyclerView(userBookings: List<UserBooking>) {
        val recyclerView = findViewById<RecyclerView>(R.id.customerRecyclerView)
        val adapter = CustomerBookingRecycleAdapter(this, userBookings,
            object : ItemClickListener {
                override fun onItemClick(position: Int) {
                    // Implementera önskat beteende för knappklick beroende på användarens roll
                    if (isAdmin) {
                        // Admin-beteende
                        removeItem(position)
                    } else {
                        // Kund-beteende
                        cancelBooking(position)
                    }
                }
            },
            isAdmin)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}