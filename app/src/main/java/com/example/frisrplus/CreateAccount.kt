package com.example.frisrplus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccount : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val registerButton: Button = findViewById(R.id.buttonRegister)
        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val firstName = findViewById<EditText>(R.id.editTextFirstName).text.toString()
        val lastName = findViewById<EditText>(R.id.editTextLastName).text.toString()
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
        val phoneNumberText = findViewById<EditText>(R.id.editTextPhoneNumber).text.toString()

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumberText.isEmpty()) {
            showToast("Vänligen fyll i alla fält")
            return
        }

        // Validate and convert the phone number
        val phoneNumber: Int = try {
            phoneNumberText.toInt()
        } catch (e: NumberFormatException) {
            showToast("Ogiltigt telefonnummer")
            return
        }

        // Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser

                    // User registered successfully
                    val user = User(firstName, lastName, email, phoneNumber)

                    // Save user data to Firestore with UID
                    saveUserToFirestore(user, firebaseUser?.uid)

                    showToast("Konto skapat framgångsrikt")

                    // Redirect to MainActivity
                    startActivity(Intent(this, MainActivity::class.java))

                    // Finish the current activity to prevent the user from coming back to the registration screen
                    finish()
                } else {
                    // If registration fails, display a message to the user.
                    // You can handle specific failure cases here.
                    val errorMessage = task.exception?.message ?: "Registrering misslyckades"
                    showToast(errorMessage)
                }
            }
    }


    private fun saveUserToFirestore(user: User, uid: String?) {
        if (uid != null) {
            // Firestore Database
            firestore.collection("Users")
                .document(uid)
                .set(user)
                .addOnSuccessListener {
                    // User data saved to Firestore successfully
                    // You can perform additional actions here if needed
                }
                .addOnFailureListener { e ->
                    // Handle errors while saving user data to Firestore
                    showToast("Fel vid sparande av användardata")
                }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
