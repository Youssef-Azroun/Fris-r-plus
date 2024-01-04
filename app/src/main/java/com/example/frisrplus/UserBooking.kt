package com.example.frisrplus

data class UserBooking(
    var bookingId: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: Long? = null, // Use Long for numeric values
    val price: String? = null,
    val selectedDate: String? = null,
    val selectedTime: String? = null,
    val typeOfCut: String? = null
)


