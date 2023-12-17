package com.example.frisrplus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        }else{
            showVerificationAlert()
        }

        // Validate and convert the phone number
        val phoneNumber: Int = try {
            phoneNumberText.toInt()
        } catch (e: NumberFormatException) {
            showToast("Ogiltigt telefonnummer")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser

                    firebaseUser?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {

                                val user = User(firstName, lastName, email, phoneNumber)
                                saveUserToFirestore(user, firebaseUser.uid)
                                showVerificationAlert()
                            } else {
                                showToast("Konto skapat, men fel vid skickande av verifierings-e-post.")
                            }
                        }
                } else {
                    // If registration fails, display a message to the user.
                    val errorMessage = task.exception?.message ?: "Registrering misslyckades"
                    showToast(errorMessage)
                }
            }
    }


    private fun showVerificationAlert() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Bekräftelse")
            .setMessage("Vi har skickat en bekräftelse till din e-post. Om du inte har fått det, Tryck på Avbryt. vänligen kontrollera om du har angett rätt e-postadress. Om du har fått bekräftelse tryck okej för att gå vidare")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                // navigate to the login screen
                startActivity(Intent(this, LogIn::class.java))
                finish()
                dialog.dismiss()
            }
            .setNegativeButton("Avbryt") { dialog, _ ->

                dialog.dismiss()
            }

        val alert: AlertDialog = builder.create()
        alert.show()
    }



    private fun saveUserToFirestore(user: User, uid: String?) {
        if (uid != null) {
            // Firestore Database
            firestore.collection("Users")
                .document(uid)
                .set(user)
                .addOnSuccessListener {
                    // User data saved to Firestore successfully

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
