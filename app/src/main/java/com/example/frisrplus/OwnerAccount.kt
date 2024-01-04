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
    private var userBookings = mutableListOf<UserBooking>()

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
        val db = Firebase.firestore
        val bookingsRef = db.collection("AllBookings")

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
                }.toMutableList()


                // Update the RecyclerView with the new data
                updateAllBookingRecyclerView(userBookings)
            }
        }
    }

    private fun removeItem(userBooking: UserBooking) {
        val uid = currentUser.uid

        val bookingId = userBooking.bookingId ?: ""
        Log.d("CancelBooking", "Canceling booking with ID: $bookingId")

        if (bookingId.isNotEmpty()) {
            // Reference to the specific booking document in Firestore
            val bookingRef = db
                .collection("AllBookings")
                .document(bookingId)

            val bookingRef2 = db.collection("UsersBookings")
                .document(uid)
                .collection("UserBookings")
                .document(bookingId)

            // Delete the document from Firestore
            bookingRef.delete()
                .addOnSuccessListener {
                    // Successfully deleted from Firestore, now update the local list
                    val updatedUserBookings = userBookings.toMutableList()
                    updatedUserBookings.remove(userBooking)
                    updateAllBookingRecyclerView(updatedUserBookings)
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

    private fun updateAllBookingRecyclerView(userBookings: List<UserBooking>) {
        val recyclerView = findViewById<RecyclerView>(R.id.ownerRecyclerView)
        val adapter = CustomerBookingRecycleAdapter(this, userBookings,
            object : ItemClickListener {
                override fun onItemClick(userBooking: UserBooking, buttonText: String) {
                    // Implement desired behavior for button click based on user role
                    if (isAdmin) {
                        // Admin behavior
                        removeItem(userBooking)
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
