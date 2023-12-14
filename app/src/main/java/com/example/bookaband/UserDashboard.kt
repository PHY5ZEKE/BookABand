package com.example.bookaband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserDashboard : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var currentUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("User Data")

        val userUid = currentUser.uid

        // Query the database using userUid to get band details
        databaseReference.child(userUid).addListenerForSingleValueEvent(object :
            ValueEventListener {
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
                    Glide.with(this@UserDashboard)
                        .load(imageURL)
                        .into(bandLogoImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })


        var btnLogout: ImageButton = findViewById(R.id.btnLogout)
        var btnBook:Button = findViewById(R.id.btnBookABand)
        var btnRequests:ImageButton = findViewById(R.id.btnRequests)
        var btnEditUser:ImageButton = findViewById(R.id.btnEditAcc)
        var btnEvents:ImageButton = findViewById(R.id.btnEvents)

        btnLogout.setOnClickListener{
            val intent = Intent(this, Landing::class.java)
            startActivity(intent)
            finish()
        }
        btnEvents.setOnClickListener{
            val intent = Intent(this, EventUser::class.java)
            startActivity(intent)
            finish()
        }
        btnBook.setOnClickListener{
            val intent = Intent(this, BandList::class.java)
            startActivity(intent)

        }
        btnRequests.setOnClickListener{
            val intent = Intent(this, MyBookingsUser::class.java)
            startActivity(intent)
        }
        btnEditUser.setOnClickListener{
            val intent = Intent(this, EditUser::class.java)
            startActivity(intent)
        }


    }
}