package com.example.bookaband

import java.io.Serializable

data class AcceptedEvents(val bandId: String,
                          val bandName: String,
                          val eventName: String,
                          val location: String,
                          val date: String,
                          val time: String,
                          val userId: String,
                          val userEmail: String,
                          val userName: String,
                          val userContact: String,
                          val userImage: String,
    val bandImage:String,
): Serializable {
    // Empty constructor
    constructor() : this("", "", "", "", "", "", "", "", "","","","")
}
