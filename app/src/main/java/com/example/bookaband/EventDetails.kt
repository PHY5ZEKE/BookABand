package com.example.bookaband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.bookaband.BookingData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EventDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        // Retrieve the BookingData object from the intent
        val selectedEvent = intent.getSerializableExtra("eventDetails") as AcceptedEvents

        // Initialize the TextViews
        val bandNameTextView: TextView = findViewById(R.id.bandNameTextView)
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



        // Populate the TextViews with labels and data from the BookingData object

        eventNameTextView.text = "Event Name: ${selectedEvent.eventName}"
        bandNameTextView.text = "Band Name: ${selectedEvent.bandName}"
        locationTextView.text = "Location: ${selectedEvent.location}"
        dateTextView.text = "Date: ${selectedEvent.date}"
        timeTextView.text = "Time: ${selectedEvent.time}"
        userNameTextView.text = "Organizer: ${selectedEvent.userName}"
        userContactTextView.text = "Contact: ${selectedEvent.userContact}"
        userEmailTextView.text = "Email: ${selectedEvent.userEmail}"

        goBackToRequestsUser.setOnClickListener{
            val intent = Intent(this, EventUser::class.java)
            startActivity(intent)
        }
        cancelRequest.setOnClickListener {
            // Assuming the bookingId is stored as a child in the database
            val databaseReference = FirebaseDatabase.getInstance().getReference("Accepted Events")

            // Retrieve bookingId from the database using the selectedBookingUser's properties
            val selectedBookingUser = intent.getSerializableExtra("eventDetails") as AcceptedEvents
            val userId = selectedBookingUser.userId
            val eventName = selectedBookingUser.eventName
            val date = selectedBookingUser.date

            databaseReference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (eventSnapshot in snapshot.children) {
                            val eventData = eventSnapshot.getValue(AcceptedEvents::class.java)
                            if (eventData != null && eventData.eventName == eventName && eventData.date == date) {
                                val eventId = eventSnapshot.key
                                if (eventId != null) {
                                    showDeleteConfirmationDialog(eventId)

                                }

                            }

                        }


                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
        }


    }
    // Add this method to your activity
    private fun showDeleteConfirmationDialog(bookingId: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Confirm Delete")
        alertDialogBuilder.setMessage("Are you sure you want to remove this event?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // User clicked "Yes," so delete the booking
            deleteBooking(bookingId)
            val intent = Intent(this, MyBookingsActivity::class.java)
            startActivity(intent)
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            // User clicked "No," so dismiss the dialog
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    private fun deleteBooking(bookingId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Accepted Events")

        // Use the child method to specify the node to delete
        databaseReference.child(bookingId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Event Removed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to remove event: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


}
