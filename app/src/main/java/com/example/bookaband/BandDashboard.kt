package com.example.bookaband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class BandDashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_band_dashboard)

        val buttonLogout: Button = findViewById(R.id.btnLogout)

        buttonLogout.setOnClickListener{
            val intent = Intent(this, Landing::class.java)
            startActivity(intent)
            finish()
        }
    }
}