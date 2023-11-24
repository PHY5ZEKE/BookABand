package com.example.bookaband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EventsBand : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReference1: DatabaseReference
    private lateinit var currentUser: FirebaseUser
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventsAdapter: AcceptedEventsAdapter
    private lateinit var eventList: MutableList<AcceptedEvents>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_band)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Accepted Events")
        databaseReference1 = FirebaseDatabase.getInstance().getReference("Band Data")

        recyclerView = findViewById(R.id.myBookingsRecyclerView)
       eventList = mutableListOf()
        eventsAdapter = AcceptedEventsAdapter(eventList) { selectedEvent -> showEventDetails(selectedEvent)
        }

        recyclerView.adapter = eventsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val userUid = currentUser.uid


        databaseReference1.child(userUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val bandName = snapshot.child("name").getValue(String::class.java)
                    val imageURL = snapshot.child("imageURL").getValue(String::class.java)

                    // Now you have the band name and image URL, you can use them directly
                    // For example, set them to ImageView and TextView in your layout
                    val bandLogoImageView: ImageView = findViewById(R.id.bandLogo)
                    val txtBandName: TextView = findViewById(R.id.txtBandName)

                    // Set band name to TextView
                    txtBandName.text = bandName

                    // Load image using Glide or any other image loading library
                    Glide.with(this@EventsBand)
                        .load(imageURL)
                        .into(bandLogoImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

        val backToUserDashboard: Button = findViewById(R.id.backToBD)

        // Get the current user's ID (you can use Firebase Authentication)
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Query Firebase to get bookings for the current user
        userId?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Accepted Events")
            databaseReference.orderByChild("bandId").equalTo(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        eventList.clear()

                        for (eventSnapshot in snapshot.children) {
                            val acceptedEvents = eventSnapshot.getValue(AcceptedEvents::class.java)
                            acceptedEvents?.let {
                                eventList.add(it)
                            }
                        }

                        eventsAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                        Log.e("MyBookingsActivity", "Error loading data from Firebase: ${error.message}")
                    }
                })
        }
        backToUserDashboard.setOnClickListener{
            val intent = Intent(this, BandDashboard::class.java)
            startActivity(intent)
        }
    }
    private fun showEventDetails(selectedEvent: AcceptedEvents) {
        // Implement the logic to show the details of the selected booking.
        // You can use a dialog, another activity, or any other UI component to display the details.
        // For example, you can create a new activity and pass the details using Intent.
        val intent = Intent(this, EventDetails::class.java)
        intent.putExtra("eventDetails", selectedEvent)
        startActivity(intent)
    }
}
