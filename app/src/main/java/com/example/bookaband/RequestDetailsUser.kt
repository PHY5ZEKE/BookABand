package com.example.bookaband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.bookaband.BookingData

class RequestDetailsUser : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_details_user)

        // Retrieve the BookingData object from the intent
        val selectedBookingUser = intent.getSerializableExtra("requestDetailsUser") as BookingData

        // Initialize the TextViews
        val eventNameTextView: TextView = findViewById(R.id.eventNameTextView)
        val locationTextView: TextView = findViewById(R.id.locationTextView)
        val dateTextView: TextView = findViewById(R.id.dateTextView)
        val timeTextView: TextView = findViewById(R.id.timeTextView)
        val userNameTextView: TextView = findViewById(R.id.userNameTextView)
        val userContactTextView: TextView = findViewById(R.id.userContactTextView)
        val userEmailTextView: TextView = findViewById(R.id.userEmailTextView)

      //Buttons
        val goBackToRequestsUser: Button = findViewById(R.id.btnBack)
        val cancelRequest: Button = findViewById(R.id.btnCancel)
        val editRequest: Button = findViewById(R.id.btnEdit)



        // Populate the TextViews with labels and data from the BookingData object
        eventNameTextView.text = "Event Name: ${selectedBookingUser.eventName}"
        locationTextView.text = "Location: ${selectedBookingUser.location}"
        dateTextView.text = "Date: ${selectedBookingUser.date}"
        timeTextView.text = "Time: ${selectedBookingUser.time}"
        userNameTextView.text = "Organizer: ${selectedBookingUser.userName}"
        userContactTextView.text = "Contact: ${selectedBookingUser.userContact}"
        userEmailTextView.text = "Email: ${selectedBookingUser.userEmail}"

       goBackToRequestsUser.setOnClickListener{
           val intent = Intent(this, MyBookingsActivity::class.java)
           startActivity(intent)
       }
    }
}
