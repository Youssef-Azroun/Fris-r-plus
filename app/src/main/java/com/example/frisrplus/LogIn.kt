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
            "Logga in om du redan har ett konto, annars klicka på Registrera för att skapa ett."
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
        // Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    showToast("Inloggning lyckades.")

                    // Redirect to MainActivity
                    startActivity(Intent(this, MainActivity::class.java))

                    // Finish the current activity to prevent the user from coming back to the login screen
                    finish()
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
