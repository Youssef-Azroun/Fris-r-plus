package com.example.frisrplus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val adressTextView: TextView = findViewById(R.id.adressTextView)
        adressTextView.text =
            "Carl Krooks Gata 6, \n" + "252 25 Helsingborg"
        val mailTextView: TextView = findViewById(R.id.mailTextView)
        mailTextView.text =
            "frisorplus.admin@gmail.com"


        val testButton: Button = findViewById(R.id.buttonToSignIn)
        testButton.setOnClickListener {

            startActivity(Intent(this, LogIn::class.java))
        }
    }
}
