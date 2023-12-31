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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class BandDetails : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserContact: String
    private lateinit var currentUserName: String
    private lateinit var currentUserEmail: String
    private lateinit var currentUserImage: String
    private lateinit var currentBandImage: String

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
        val locationEditText: EditText = findViewById(R.id.locationEditText)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            currentUserEmail = currentUser.email ?: ""

            // Retrieve user data from the "UserData" node
            val userDataReference = FirebaseDatabase.getInstance().getReference("User Data").child(userId)
            userDataReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        currentUserName = snapshot.child("name").getValue(String::class.java) ?: ""
                        currentUserContact =
                            snapshot.child("contact").getValue(String::class.java) ?: ""
                        currentUserImage = snapshot.child("imageURL").getValue(String::class.java) ?: ""
                        // Set values to TextViews and ImageView
                        nameTextView.text = bandDetails.name
                        genreTextView.text = bandDetails.genre
                        priceTextView.text = "$${bandDetails.price}"
                        descriptionTextView.text = bandDetails.desc
                        emailTextView.text = currentUserEmail // Display the user's email
                        currentBandImage = bandDetails.imageURL.toString()
                        // Load image using Glide (ensure to handle null imageURL)
                        Glide.with(this@BandDetails)
                            .load(bandDetails.imageURL)
                            .into(imageView)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })
        }

        btnBack.setOnClickListener {
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
    fun onBookBandClick(view: android.view.View) {
        val bandDetails: BandData = intent.getSerializableExtra("bandDetails") as BandData
        val userId = auth.currentUser?.uid

        val eventNameEditText = findViewById<EditText>(R.id.eventNameEditText)
        val locationEditText = findViewById<EditText>(R.id.locationEditText)
        val dateEditText = findViewById<EditText>(R.id.dateEditText)
        val timeEditText = findViewById<EditText>(R.id.timeEditText)

        val eventName = eventNameEditText.text.toString().trim()
        val location = locationEditText.text.toString().trim()
        val date = dateEditText.text.toString().trim()
        val time = timeEditText.text.toString().trim()

        // Validate input fields
        if (eventName.isNotEmpty() && location.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {

            if (eventName.length > 60 || location.length > 60) {
                Toast.makeText(this, "Event name and location should be within 1-60 characters.", Toast.LENGTH_SHORT).show()
                return
            }

            val currentDate = Calendar.getInstance()
            val selectedDate = Calendar.getInstance()
            val dateParts = date.split("-")
            selectedDate.set(dateParts[2].toInt(), dateParts[1].toInt() - 1, dateParts[0].toInt())

            if (selectedDate.after(currentDate)) {
                // Proceed with booking since the selected date is valid

                val bookingData = BookingData(
                    bandId = bandDetails.userUid,
                    bandName = bandDetails.name,
                    eventName = eventName,
                    location = location,
                    date = date,
                    time = time,
                    userId = userId.toString(),
                    userEmail = currentUserEmail,
                    userName = currentUserName,
                    userContact = currentUserContact,
                    userImage = currentUserImage,
                    bandImage = currentBandImage
                )

                val databaseReference = FirebaseDatabase.getInstance().getReference("Bookings")
                val bookingId = databaseReference.push().key

                if (bookingId != null) {
                    databaseReference.child(bookingId).setValue(bookingData)

                    val notificationReference = FirebaseDatabase.getInstance().getReference("Notifications").child(bandDetails.userUid)
                    val notificationId = notificationReference.push().key

                    val notificationData = mapOf(
                        "title" to "New Booking Request",
                        "message" to "You have a new booking request for $eventName",
                        // Add more data if needed
                    )

                    if (notificationId != null) {
                        // Store notification under the specific band's user ID
                        notificationReference.child(notificationId).setValue(notificationData)
                    }

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
                // Show an error toast message if the selected date is not valid
                Toast.makeText(this, "Please select a future date", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Show an error toast message if any field is empty
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }
}
