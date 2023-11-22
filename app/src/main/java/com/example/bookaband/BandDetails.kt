package com.example.bookaband

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar


class BandDetails : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_band_details)

        auth = FirebaseAuth.getInstance()

        // Retrieve BandData object from the intent
        val bandDetails: BandData = intent.getSerializableExtra("bandDetails") as BandData

        // Get references to TextViews and ImageView
        val nameTextView: TextView = findViewById(R.id.detailNameTextView)
        val genreTextView: TextView = findViewById(R.id.detailGenreTextView)
        val priceTextView: TextView = findViewById(R.id.detailPriceTextView)
        val descriptionTextView: TextView = findViewById(R.id.detailDescriptionTextView)
        val emailTextView: TextView = findViewById(R.id.detailEmailTextView)
        val imageView: ImageView = findViewById(R.id.detailImageView)
        val btnBack: Button = findViewById(R.id.backButton)
        //EditText
        val dateEditText: EditText = findViewById(R.id.dateEditText)
        val timeEditText: EditText = findViewById(R.id.timeEditText)
        val eventNameEditText: EditText = findViewById(R.id.eventNameEditText)
        val LocationEditText: EditText = findViewById(R.id.locationEditText)

        // Set values to TextViews and ImageView
        nameTextView.text = bandDetails.name
        genreTextView.text = bandDetails.genre
        priceTextView.text = "$${bandDetails.price}"
        descriptionTextView.text = bandDetails.desc
        emailTextView.text = bandDetails.email

        // Load image using Glide (ensure to handle null imageURL)
        Glide.with(this)
            .load(bandDetails.imageURL)
            .into(imageView)

        btnBack.setOnClickListener{
            val intent = Intent(this, BandList::class.java)
            startActivity(intent)
        }
        dateEditText.setOnClickListener {
            showDatePicker()
        }

        timeEditText.setOnClickListener {
            showTimePicker()
        }
    }
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val date = "$day-${month + 1}-$year"
                findViewById<EditText>(R.id.dateEditText).setText(date)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                val time = String.format("%02d:%02d", hourOfDay, minute)
                findViewById<EditText>(R.id.timeEditText).setText(time)
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }


    // Method to handle the "Book Band" button click
    // ...

    fun onBookBandClick(view: android.view.View) {
        val bandDetails: BandData = intent.getSerializableExtra("bandDetails") as BandData
        val userId = auth.currentUser?.uid

        val eventNameEditText = findViewById<EditText>(R.id.eventNameEditText)
        val locationEditText = findViewById<EditText>(R.id.locationEditText)
        val dateEditText = findViewById<EditText>(R.id.dateEditText)
        val timeEditText = findViewById<EditText>(R.id.timeEditText)

        val eventName = eventNameEditText.text.toString()
        val location = locationEditText.text.toString()
        val date = dateEditText.text.toString()
        val time = timeEditText.text.toString()

        // Validate input fields
        if (eventName.isNotEmpty() && location.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
            val bookingData = BookingData(
                bandId = bandDetails.userUid,
                eventName = eventName,
                location = location,
                date = date,
                time = time,
                userId = userId.toString()
            )

            val databaseReference = FirebaseDatabase.getInstance().getReference("Bookings")
            val bookingId = databaseReference.push().key

            if (bookingId != null) {
                databaseReference.child(bookingId).setValue(bookingData)

                // Clear input fields
                eventNameEditText.text.clear()
                locationEditText.text.clear()
                dateEditText.text.clear()
                timeEditText.text.clear()

                // Show toast message
                Toast.makeText(this, "Booking Requested", Toast.LENGTH_SHORT).show()

                // Navigate back to UserDashboard activity
                val intent = Intent(this, UserDashboard::class.java)
                startActivity(intent)
            }
        } else {
            // Show an error toast message if any field is empty
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }


}
