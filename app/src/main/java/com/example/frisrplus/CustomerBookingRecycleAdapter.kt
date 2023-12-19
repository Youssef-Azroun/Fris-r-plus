package com.example.frisrplus

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomerBookingRecycleAdapter(private val context: Context, private val userBookings: List<UserBooking>) :
    RecyclerView.Adapter<CustomerBookingRecycleAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.customer_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userBooking = userBookings[position]

        // Populate your ViewHolder views with data from userBooking
        holder.emailTextView.text = userBooking.email
        holder.firstNameTextView.text = userBooking.firstName
        holder.lastNameTextView.text = userBooking.lastName
        holder.phoneNumberTextView.text = userBooking.phoneNumber.toString()
        holder.priceTextView.text = userBooking.price.toString()
        holder.dateTextView.text = userBooking.selectedDate
        holder.timeTextView.text = userBooking.selectedTime
        holder.cutTypeTextView.text = userBooking.typeOfCut
    }

    override fun getItemCount(): Int {
        return userBookings.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        val firstNameTextView: TextView = itemView.findViewById(R.id.firstNameTextView)
        val lastNameTextView: TextView = itemView.findViewById(R.id.lastNameTextView)
        val phoneNumberTextView: TextView = itemView.findViewById(R.id.phoneNumberTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val cutTypeTextView: TextView = itemView.findViewById(R.id.cutTypeTextView)
    }
}
