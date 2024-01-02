package com.example.frisrplus

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

        // Check if the user is already logged in
        val currentUser = auth.currentUser

        if (currentUser != null && currentUser.isEmailVerified) {
            // Retrieve user data from Firestore
            FirebaseFirestore.getInstance().collection("Users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userData = document.toObject(User::class.java)
                        userData?.let {
                            if (it.admin == true) {
                                // Save admin flag in SharedPreferences
                                saveAdminFlag(true)
                                // User is an admin, navigate to OwnerAccount
                                startActivity(Intent(this, OwnerAccount::class.java))
                                finish()
                            } else {
                                // Save admin flag in SharedPreferences
                                saveAdminFlag(false)
                                // User is not an admin, navigate to MainActivity
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                        }
                    }
                }
        } else {
            // User is not logged in or not verified, navigate to Main
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun saveAdminFlag(isAdmin: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isAdmin", isAdmin)
        editor.apply()
    }
}