package com.example.frisrplus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    var services = mutableListOf<Services>(
                                 Services("Barn klippning", "100kr."),
                                 Services("Pensionär klippning", "100kr."),
                                 Services("Vuxen hår klippning", " från 100kr."),
                                 Services("Vuxen skägg klippning", "från 70kr."),
                                 Services("Hår+Skägg klippning", "från 150kr.")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Check the user's login status
        updateLoginStatus()


        var recyclerView = findViewById<RecyclerView>(R.id.servicesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = ServicesRecycleAdapter(this, services)
        recyclerView.adapter = adapter

        val adressTextView: TextView = findViewById(R.id.adressTextView)
        adressTextView.text =
            "Carl Krooks Gata 6, \n" + "252 25 Helsingborg"
        val mailTextView: TextView = findViewById(R.id.mailTextView)
        mailTextView.text =
            "frisorplus.admin@gmail.com"

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
}
