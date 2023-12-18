package com.example.frisrplus

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServicesRecycleAdapter (val context: Context, val services : List<Services> ) : RecyclerView.Adapter<ServicesRecycleAdapter.ViewHolder>() {

    var layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.services_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return services.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val services = services[position]

        holder.servicedNameTextView.text = services.servicedName
        holder.priceTextView.text = services.price

        // Set OnClickListener for the "Boka" button
        holder.bookingButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DayAndTimeForBookingAvailableActivity::class.java)
            // You may want to pass some data to DayAndTimeForBookingAvailableActivity using intent.putExtra if needed
            context.startActivity(intent)
        }
    }


    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var servicedNameTextView = itemView.findViewById<TextView>(R.id.servicedNameTextView)
        var priceTextView = itemView.findViewById<TextView>(R.id.priceTextView)
        var bookingButton = itemView.findViewById<Button>(R.id.bookingButton)
    }
}