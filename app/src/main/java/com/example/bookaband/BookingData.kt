package com.example.bookaband


data class BookingData(
    val bandId: String,
    val eventName: String,
    val location: String,
    val date: String,
    val time: String,
    val userId: String,
    val userEmail: String,
    val userName: String,
    val userContact: String
) {
    // Empty constructor
    constructor() : this("", "", "", "", "", "", "", "", "")
}
