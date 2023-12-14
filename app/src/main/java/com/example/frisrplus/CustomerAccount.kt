package com.example.frisrplus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class CustomerAccount : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_account)

        auth = FirebaseAuth.getInstance()

        // Set up other UI elements in your CustomerAccount activity

        // Set up logout button
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Call the logout function
            logout()
        }
    }

    private fun logout() {
        // Log out the user using Firebase Authentication
        auth.signOut()

        // Redirect to the login activity after logout
        startActivity(Intent(this, LogIn::class.java))

        // Finish the current activity to prevent the user from coming back to the logged-in state
        finish()
    }
}
