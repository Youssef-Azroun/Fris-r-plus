package com.example.frisrplus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

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
        goToLoggInTextView.text =
            "Logga in"
        goToLoggInTextView.setOnClickListener {
            startActivity(Intent(this, LogIn::class.java))
        }


    }
}
