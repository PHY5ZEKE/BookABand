package com.example.bookaband

data class User(
    val userUid: String,
    val name: String,
    val contact: String,
    val desc: String,
    val imageURL: String?
) {
    // Empty constructor
    constructor() : this("", "", "", "",  null)
}