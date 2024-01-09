package com.example.frisrplus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextPhoneNumber: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!

        editTextFirstName = findViewById(R.id.newFirstNameTextView)
        editTextLastName = findViewById(R.id.newLastNameTextView)
        editTextPhoneNumber = findViewById(R.id.newPhoneNumberTextView)

        loadUserInformation(currentUser.uid)

        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            saveProfile(currentUser.uid)
        }
    }

    private fun loadUserInformation(uid: String) {
        val db = Firebase.firestore
        val userRef = db.collection("Users").document(uid)

        userRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject<User>()
                if (user != null) {
                    // Pre-fill EditText fields with user information
                    editTextFirstName.setText(user.firstName)
                    editTextLastName.setText(user.lastName)
                    editTextPhoneNumber.setText(user.phoneNumber.toString())
                }
            }
        }.addOnFailureListener { e ->
            Log.e("EditProfile", "Error fetching user data: $e")
        }
    }
    private fun saveProfile(uid: String) {
        val db = Firebase.firestore
        val userRef = db.collection("Users").document(uid)

        val newFirstName = editTextFirstName.text.toString().trim()
        val newLastName = editTextLastName.text.toString().trim()
        val newPhoneNumber = editTextPhoneNumber.text.toString().trim()

        // Update user information in Firestore
        userRef.update(
            "firstName", newFirstName,
            "lastName", newLastName,
            "phoneNumber", newPhoneNumber
        ).addOnSuccessListener {
            // Successfully updated user information
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            finish() // Close the EditProfileActivity
        }.addOnFailureListener { e ->
            Log.e("EditProfile", "Error updating user data: $e")
            // Handle the failure case if needed
        }
    }

}
