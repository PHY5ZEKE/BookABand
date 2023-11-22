package com.example.bookaband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyBookingsBand : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var bookingList: MutableList<BookingData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bookings)

        recyclerView = findViewById(R.id.myBookingsRecyclerView)
        bookingList = mutableListOf()
        bookingAdapter = BookingAdapter(bookingList)

        recyclerView.adapter = bookingAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val backToUserDashboard: Button = findViewById(R.id.backToUD)

        // Get the current user's ID (you can use Firebase Authentication)
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Query Firebase to get bookings for the current user
        userId?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Bookings")
            databaseReference.orderByChild("bandId").equalTo(it)
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
}
