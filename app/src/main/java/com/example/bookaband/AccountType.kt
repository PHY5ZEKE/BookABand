package com.example.bookaband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class AccountType : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_type)



        var btnBand: Button = findViewById(R.id.btnBand)
        var btnOrganizer: Button = findViewById(R.id.btnBand)

        btnBand.setOnClickListener{
            val intent = Intent(this, CreateBand::class.java)
            startActivity(intent)
        }
        btnOrganizer.setOnClickListener{
            val intent = Intent(this, CreateUser::class.java)
            startActivity(intent)
        }
    }


}

