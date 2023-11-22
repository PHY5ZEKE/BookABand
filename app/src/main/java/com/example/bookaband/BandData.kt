package com.example.bookaband

import java.io.Serializable

data class BandData(
    val userUid: String,
    val name: String,
    val email: String,
    val genre: String,
    val desc: String,
    val price: Float,
    val imageURL: String?
):Serializable {
    // Empty constructor
    constructor() : this("", "", "", "", "", 0.0f, null)
}