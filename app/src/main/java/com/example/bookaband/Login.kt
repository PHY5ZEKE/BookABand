package com.example.bookaband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bookaband.databinding.ActivityLoginBinding
import com.example.bookaband.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        checkUserDataAndNavigate()
                    } else {
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserDataAndNavigate() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val databaseReference = FirebaseDatabase.getInstance().getReference("Band Data").child(userId)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.hasChildren()) {
                        // User has stored data, navigate to MainActivity
                        val intent = Intent(this@Login, BandList::class.java)
                        startActivity(intent)

                    } else {
                        // User doesn't have stored data or has empty data, navigate to AccountType
                        val intent = Intent(this@Login, AccountType::class.java)
                        startActivity(intent)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error if needed
                    Log.e("CheckUserData", "Error accessing database: ${error.message}")
                    Toast.makeText(this@Login, "Error accessing database", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
 


