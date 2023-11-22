package com.example.bookaband

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class BandList : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var bandAdapter: BandAdapter
    private lateinit var bandList: MutableList<BandData>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_band_list)

        val btnBack: Button = findViewById(R.id.btnBack)
        recyclerView = findViewById(R.id.recyclerView)
        bandList = mutableListOf()
        bandAdapter = BandAdapter(bandList)

        recyclerView.adapter = bandAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseReference = FirebaseDatabase.getInstance().getReference("Band Data")
        loadData()

       btnBack.setOnClickListener{
            val intent = Intent(this, UserDashboard::class.java)
            startActivity(intent)
        }

    }

    private fun loadData() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bandList.clear()

                for (currentDate in snapshot.children) {
                    for (bandSnapshot in currentDate.children) {
                        val bandInfo = bandSnapshot.getValue(BandData::class.java)
                        bandInfo?.let {
                            bandList.add(it)
                        } ?: run {
                            Log.e("MainActivity", "Failed to parse BandInfo from snapshot: $bandSnapshot")
                        }
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

}
