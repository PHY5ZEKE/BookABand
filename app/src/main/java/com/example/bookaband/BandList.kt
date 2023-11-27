package com.example.bookaband

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class BandList : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var bandAdapter: BandAdapter
    private lateinit var bandList: MutableList<BandData>
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var currentUser: FirebaseUser
    private lateinit var databaseReference1: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_band_list)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        databaseReference1 = FirebaseDatabase.getInstance().getReference("User Data")

        val userUid = currentUser.uid

        // Query the database using userUid to get band details
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
                    Glide.with(this@BandList)
                        .load(imageURL)
                        .into(bandLogoImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })


        val btnSearch: SearchView = findViewById(R.id.search)
        val btnBack: Button = findViewById(R.id.btnBack)
        recyclerView = findViewById(R.id.recyclerView)
        bandList = mutableListOf()
        bandAdapter = BandAdapter(bandList){selectedBand->showBandDetails(selectedBand)}

        recyclerView.adapter = bandAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseReference = FirebaseDatabase.getInstance().getReference("Band Data")
        loadData()

       btnBack.setOnClickListener{
            val intent = Intent(this, UserDashboard::class.java)
            startActivity(intent)
        }
        btnSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { performSearch(it) }
                return false
            }
        })

    }

    private fun loadData() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bandList.clear()

                for (childSnapshot in snapshot.children) {
                    val bandInfo = childSnapshot.getValue(BandData::class.java)
                    bandInfo?.let {
                        bandList.add(it)
                    } ?: run {
                        Log.e("MainActivity", "Failed to parse BandInfo from snapshot: $childSnapshot")
                    }
                }

                runOnUiThread {
                    bandAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("MainActivity", "Error loading data from Firebase: ${error.message}")
            }
        })
    }

    private fun performSearch(query: String) {
        val searchQuery = query.lowercase()

        val filteredList = bandList.filter { band ->
            band.name.lowercase().contains(searchQuery)
        }

        bandAdapter = BandAdapter(filteredList) { selectedBand ->
            showBandDetails(selectedBand)
        }

        recyclerView.adapter = bandAdapter
        bandAdapter.notifyDataSetChanged()
    }

    private fun showBandDetails(selectedBand: BandData) {
        // Implement the logic to show the details of the selected band.
        // You can use a dialog, another activity, or any other UI component to display the details.
        // For example, you can create a new activity and pass the details using Intent.
        val intent = Intent(this, BandDetails::class.java)
        intent.putExtra("bandDetails", selectedBand)
        startActivity(intent)
    }

}
