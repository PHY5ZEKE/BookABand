package com.example.bookaband

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.FirebaseApp

class Landing : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        FirebaseApp.initializeApp(this)


        var logBand: Button = findViewById(R.id.btnLogBand)
        var txtReg: Button = findViewById(R.id.RegisterLanding)


        logBand.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        txtReg.setOnClickListener{
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}