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
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Check the user's login status
        updateLoginStatus()

        val currentDate = Calendar.getInstance().timeInMillis
        val formattedDate = formatDate(currentDate)

        // Call the function to listen for booking changes
        listenForBookingChanges(currentDate, formattedDate)

        var recyclerView = findViewById<RecyclerView>(R.id.servicesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = ServicesRecycleAdapter(this, servicesDataManager.services)
        recyclerView.adapter = adapter


        val goToLoggInTextView: TextView = findViewById(R.id.goToLoggIntextView)
        goToLoggInTextView.setOnClickListener {
            // Check if the user is logged in
            if (auth.currentUser == null) {
                // User is not logged in, go to login activity
                startActivity(Intent(this, LogIn::class.java))
            } else {
                // User is logged in, go to customer account activity
                startActivity(Intent(this, CustomerAccount::class.java))
            }
        }


    }

    private fun updateLoginStatus() {
        // Get the current user
        val user: FirebaseUser? = auth.currentUser

        // Get the reference to the TextView
        val goToLoggInTextView: TextView = findViewById(R.id.goToLoggIntextView)

        // Check if the user is logged in
        if (user != null) {
            // User is logged in, change the text
            goToLoggInTextView.text = "Mitt konto"
        } else {
            // User is not logged in, keep the original text
            goToLoggInTextView.text = "Logga in"
        }
    }

    private fun listenForBookingChanges(selectedDate: Long, formattedDate: String) {
        // Create a reference to the Firestore collection for AllBookings
        val allBookingsCollection = Firebase.firestore.collection("AllBookings")
            .whereEqualTo("selectedDate", formattedDate)

        // Add a snapshot listener to the AllBookings collection
        allBookingsCollection.addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("!!!", "Error listening for booking changes: ${error.message}")
                return@addSnapshotListener
            }

            // Handle deletions from AllBookings
            val documentChanges = value?.documentChanges
            documentChanges?.forEach { documentChange ->
                if (documentChange.type == DocumentChange.Type.REMOVED) {
                    val removedDocument = documentChange.document
                    val removedTime = removedDocument["selectedTime"] as String

                    // Delete the corresponding document from UserBookings
                    deleteDocumentFromUserBookings(removedTime, formattedDate)
                }
            }
        }
    }

    private fun deleteDocumentFromUserBookings(selectedTime: String, selectedDate: String) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val uid = currentUser.uid

            // Query Firestore to get the document ID to delete from UserBookings
            Firebase.firestore.collection("UsersBookings")
                .document(uid)
                .collection("UserBookings")
                .whereEqualTo("selectedDate", selectedDate)
                .whereEqualTo("selectedTime", selectedTime)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Delete the document from UserBookings
                        Firebase.firestore.collection("UsersBookings")
                            .document(uid)
                            .collection("UserBookings")
                            .document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Log.d("!!!", "Document deleted from UserBookings")
                            }
                            .addOnFailureListener { e ->
                                Log.e("!!!", "Error deleting document from UserBookings: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("!!!", "Error querying UserBookings: ${e.message}")
                }
        }
    }
    private fun formatDate(dateInMillis: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(dateInMillis))
    }
}
