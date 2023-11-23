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

class MyBookingsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var bookingList: MutableList<BookingData>
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bookings)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("User Data")

        recyclerView = findViewById(R.id.myBookingsRecyclerView)
        bookingList = mutableListOf()
        bookingAdapter = BookingAdapter(bookingList){selectedBookingUser->showBookingDetailsUser(selectedBookingUser)}

        recyclerView.adapter = bookingAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val userUid = currentUser.uid

        // Query the database using userUid to get band details
        databaseReference.child(userUid).addListenerForSingleValueEvent(object : ValueEventListener {
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
                    Glide.with(this@MyBookingsActivity)
                        .load(imageURL)
                        .into(bandLogoImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

        val backToUserDashboard: Button = findViewById(R.id.backToUD)

        // Get the current user's ID (you can use Firebase Authentication)
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Query Firebase to get bookings for the current user
        userId?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Bookings")
            databaseReference.orderByChild("userId").equalTo(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        bookingList.clear()

                        for (bookingSnapshot in snapshot.children) {
                            val bookingData = bookingSnapshot.getValue(BookingData::class.java)
                            bookingData?.let {
                                bookingList.add(it)
                            }
                        }

                        bookingAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                        Log.e("MyBookingsActivity", "Error loading data from Firebase: ${error.message}")
                    }
                })
        }
        backToUserDashboard.setOnClickListener{
            val intent = Intent(this, UserDashboard::class.java)
            startActivity(intent)
        }
    }
    private fun showBookingDetailsUser(selectedBookingUser: User) {
        // Implement the logic to show the details of the selected band.
        // You can use a dialog, another activity, or any other UI component to display the details.
        // For example, you can create a new activity and pass the details using Intent.
        val intent = Intent(this, BandDetails::class.java)
        intent.putExtra("bandDetails", selectedBookingUser)
        startActivity(intent)
    }

}
