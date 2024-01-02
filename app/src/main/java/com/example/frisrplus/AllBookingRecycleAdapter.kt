package com.example.frisrplus

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AllBookingRecycleAdapter ( val context: Context, val userBookings: List<UserBooking>) :
    RecyclerView.Adapter<AllBookingRecycleAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.owner_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userBookings.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userBooking = userBookings[position]
        holder.allUserBookingInfoTextView.text = "Name: ${userBooking.firstName} ${userBooking.lastName}\nE-post: ${userBooking.email}\n" +
                "Tel: 0${userBooking.phoneNumber.toString()}\nPris: ${userBooking.price.toString()}" +
                "\nDatum: ${userBooking.selectedDate}\nTid: ${userBooking.selectedTime}\nTyp: ${userBooking.typeOfCut}"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val allUserBookingInfoTextView: TextView = itemView.findViewById(R.id.allUserBookingInfoTextView)
    }
}