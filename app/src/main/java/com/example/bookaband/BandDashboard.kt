package com.example.bookaband

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class BandDashboard : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_band_dashboard)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Band Data")

        val buttonLogout: Button = findViewById(R.id.btnLogout)
        val buttonRequest: Button = findViewById(R.id.btnRequests)
        val buttonEditBand: Button = findViewById(R.id.btnEditAcc)

        buttonLogout.setOnClickListener {
            val intent = Intent(this, Landing::class.java)
            startActivity(intent)
            finish()
        }
        buttonRequest.setOnClickListener {
            val intent = Intent(this, MyBookingsBand::class.java)
            startActivity(intent)

        }
        buttonEditBand.setOnClickListener{
            val intent = Intent(this, EditBand::class.java)
            startActivity(intent)
        }


        // Assuming userUid is the UID of the current logged-in band
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
                    Glide.with(this@BandDashboard)
                        .load(imageURL)
                        .into(bandLogoImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }
}
