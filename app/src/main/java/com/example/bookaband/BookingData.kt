package com.example.bookaband

import java.io.Serializable


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
):Serializable {
    // Empty constructor
    constructor() : this("", "", "", "", "", "", "", "", "")
}
