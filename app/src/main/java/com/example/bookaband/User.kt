package com.example.bookaband

import java.io.Serializable

data class User(
    val userUid: String,
    val name: String,
    val contact: String,
    val desc: String,
    val imageURL: String?
):Serializable{
    // Empty constructor
    constructor() : this("", "", "", "",  null)
}