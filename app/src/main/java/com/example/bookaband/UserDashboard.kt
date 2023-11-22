package com.example.bookaband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class UserDashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        var btnLogout: Button = findViewById(R.id.btnLogout)
        var btnBook:Button = findViewById(R.id.btnBookABand)

        btnLogout.setOnClickListener{
            val intent = Intent(this, Landing::class.java)
            startActivity(intent)
            finish()
        }
        btnBook.setOnClickListener{
            val intent = Intent(this, BandList::class.java)
            startActivity(intent)

        }


    }
}