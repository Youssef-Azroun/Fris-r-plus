package com.example.frisrplus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LogIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        auth = FirebaseAuth.getInstance()

        setupLoginAndRegisterButtons()

        // Display a message with login and registration information
        val messageTextView: TextView = findViewById(R.id.textViewMessage)
        messageTextView.text =
            "Logga in om du har ett konto om du inte har ett konto klicka på registrera för att \nskapa ett konto."
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
        // Convert the provided email to lowercase for case-insensitive comparison
        val lowercaseEmail = email.toLowerCase()

        // Check if the user is logging in with the specified credentials
        if (lowercaseEmail == "frisorplus@gmail.com" && password == "adminfp4312") {
            // Navigate to OwnerAccount activity
            startActivity(Intent(this, OwnerAccount::class.java))
            // Finish the current activity to prevent the user from coming back to the login screen
            finish()
            return
        }

        // For other logins, proceed with Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // Login successful and email is verified
                        showToast("Inloggning lyckades.")

                        // Redirect to MainActivity
                        startActivity(Intent(this, MainActivity::class.java))

                        // Finish the current activity to prevent the user from coming back to the login screen
                        finish()
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
