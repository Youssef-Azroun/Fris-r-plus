package com.example.frisrplus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class CustomerAccount : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    val userList = mutableListOf<User>()

    lateinit var currentUser: FirebaseUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_account)

        val db = Firebase.firestore
        val docRef = db.collection("Users")

        auth = FirebaseAuth.getInstance()

        currentUser = auth.currentUser!!

        docRef.addSnapshotListener { snapshot , e ->
            if (snapshot != null){
                userList.clear()
                for (document in snapshot.documents){
                    val item = document.toObject<User>()
                    if (item != null){
                        userList.add(item)
                    }
                }
            }
            printUserList()
        }

        // Set up logout button
        val logOutTextView: TextView = findViewById(R.id.logOutTextView)
        logOutTextView.setOnClickListener {
            // Call the logout function
            logout()
        }
        getUserInformation(currentUser.uid)
    }

    private fun getUserInformation(uid: String) {
        val db = Firebase.firestore
        val userRef = db.collection("Users").document(uid)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val user = document.toObject<User>()
                if (user != null) {
                    // Visa den specifika informationen om den inloggade användaren
                    val userNameTextView: TextView = findViewById(R.id.textViewMyname)
                    userNameTextView.text = "${user.firstName} ${user.lastName}\n\n${user.email}\n\n0${user.phoneNumber}"
                }
            } else {
                Log.d("!!!", "Användardokumentet finns inte")
            }
        }.addOnFailureListener { e ->
            Log.e("!!!", "Fel vid hämtning av användardata: $e")
        }
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

    fun printUserList() {
        for (item in userList){
            Log.d("!!!", "${item.firstName}")
        }
    }
}
