package com.example.frisrplus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val testButton: Button = findViewById(R.id.buttonToSignIn)
        testButton.setOnClickListener {

            startActivity(Intent(this, LogIn::class.java))
        }
    }
}
