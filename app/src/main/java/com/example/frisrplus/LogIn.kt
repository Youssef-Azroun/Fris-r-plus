package com.example.frisrplus

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore

class LogIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

        setupLoginAndRegisterButtons()
    }

    private fun saveAdminFlag(isAdmin: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isAdmin", isAdmin)
        editor.apply()
    }

    private fun isAdmin(): Boolean {
        return sharedPreferences.getBoolean("isAdmin", false)
    }

    private fun setupLoginAndRegisterButtons() {
        val loginButton: Button = findViewById(R.id.buttonLogin)
        val registerButton: Button = findViewById(R.id.buttonregester)
        val emailEditText: EditText = findViewById(R.id.editTextEmail)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Var god fyll i e-postadress och lösenord.")
            } else {
                loginUser(email, password)
            }
        }

        registerButton.setOnClickListener {
            // Redirect to the registration activity
            startActivity(Intent(this, CreateAccount::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // Retrieve user data from Firestore
                        FirebaseFirestore.getInstance().collection("Users")
                            .document(user.uid)
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
                                            // Finish the current activity to prevent the user from coming back to the login screen
                                            finish()
                                        } else {
                                            // Save admin flag in SharedPreferences
                                            saveAdminFlag(false)
                                            // User is not an admin, navigate to MainActivity
                                            startActivity(Intent(this, MainActivity::class.java))
                                            // Finish the current activity to prevent the user from coming back to the login screen
                                            finish()
                                        }
                                    }
                                } else {
                                    // User data not found in Firestore
                                    showToast("User data not found.")
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Handle Firestore query failure
                                showToast("Failed to retrieve user data from Firestore.")
                                Log.e("LoginActivity", "Error getting user data", exception)
                            }
                    } else {
                        // If the user is not verified, show a message
                        showToast("Var god verifiera din e-post innan du loggar in.")
                    }
                } else {
                    // If login fails, display a message to the user.
                    handleLoginFailure(task.exception)
                }
            }
    }

    private fun handleLoginFailure(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidUserException -> showToast("Felaktig e-post eller lösenord.")
            is FirebaseAuthInvalidCredentialsException -> showToast("Felaktig e-postadress eller lösenord.")
            else -> showToast("Inloggningen misslyckades.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}


