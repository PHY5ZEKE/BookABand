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

class RequestDetailsUser : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_details_user)

        // Retrieve the BookingData object from the intent
        val selectedBookingUser = intent.getSerializableExtra("requestDetailsUser") as BookingData

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

        eventNameTextView.text = "Event Name: ${selectedBookingUser.eventName}"
        bandNameTextView.text = "Band Name: ${selectedBookingUser.bandName}"
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
        cancelRequest.setOnClickListener {
            // Assuming the bookingId is stored as a child in the database
            val databaseReference = FirebaseDatabase.getInstance().getReference("Bookings")

            // Retrieve bookingId from the database using the selectedBookingUser's properties
            val selectedBookingUser = intent.getSerializableExtra("requestDetailsUser") as BookingData
            val userId = selectedBookingUser.userId
            val eventName = selectedBookingUser.eventName
            val date = selectedBookingUser.date

            databaseReference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (bookingSnapshot in snapshot.children) {
                            val bookingData = bookingSnapshot.getValue(BookingData::class.java)
                            if (bookingData != null && bookingData.eventName == eventName && bookingData.date == date) {
                                val bookingId = bookingSnapshot.key
                                if (bookingId != null) {
                                    showDeleteConfirmationDialog(bookingId)

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
        alertDialogBuilder.setMessage("Are you sure you want to cancel this booking?")
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
        val databaseReference = FirebaseDatabase.getInstance().getReference("Bookings")

        // Use the child method to specify the node to delete
        databaseReference.child(bookingId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Booking canceled successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to cancel booking: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


}
