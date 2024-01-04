package com.example.frisrplus

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

interface ItemClickListener {
    fun onItemClick(userBooking: UserBooking)
}


class CustomerBookingRecycleAdapter(private val context: Context,
                                    private val userBookings: List<UserBooking>,
                                    private val itemClickListener: ItemClickListener,
                                    private val isAdmin: Boolean) :
    RecyclerView.Adapter<CustomerBookingRecycleAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.customer_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userBooking = userBookings[position]

        // Set the button text to "Ta bort" for the admin
        if (isAdmin) {
            holder.cancelButton.text = "Ta bort"
        } else {
            // Check if the booking exists in AllBookings for non-admin users
            checkBookingExistsInAllBookings(userBooking.bookingId, holder)
        }

        // Klickhanterare för knappen
        holder.cancelButton.setOnClickListener {
            itemClickListener.onItemClick(userBooking)
        }

        // Populate your ViewHolder views with data from userBooking
        holder.custumerBookingInfoTextView.text = "Name: ${userBooking.firstName} ${userBooking.lastName}\nE-post: ${userBooking.email}\n" +
                "Tel: 0${userBooking.phoneNumber.toString()}\nPris: ${userBooking.price.toString()}" +
                "\nDatum: ${userBooking.selectedDate}\nTid: ${userBooking.selectedTime}\nTyp: ${userBooking.typeOfCut}"
    }


    private fun checkBookingExistsInAllBookings(bookingId: String?, holder: ViewHolder) {
        if (bookingId != null) {
            val allBookingsRef = Firebase.firestore.collection("AllBookings").document(bookingId)

            allBookingsRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Booking exists in AllBookings, set button text to "Avboka"
                        holder.cancelButton.text = "Avboka"
                    } else {
                        // Booking doesn't exist in AllBookings, set button text to "Ta bort"
                        holder.cancelButton.text = "Ta bort"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("CheckBookingExists", "Error checking booking in AllBookings: $e")
                }
        } else {
            Log.e("CheckBookingExists", "Invalid bookingId")
        }
    }


    override fun getItemCount(): Int {
        return userBookings.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val custumerBookingInfoTextView: TextView = itemView.findViewById(R.id.custumerBookingInfoTextView)
        val cancelButton: Button = itemView.findViewById(R.id.cancelButton)
    }
}
