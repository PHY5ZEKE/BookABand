package com.example.bookaband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bookaband.databinding.ActivityLoginUserBinding
import com.example.bookaband.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class LoginUser : AppCompatActivity() {
    private lateinit var binding: ActivityLoginUserBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        binding.signInUser.setOnClickListener {
            val email = binding.emailEtUser.text.toString()
            val pass = binding.passETUser.text.toString()

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
            val databaseReference = FirebaseDatabase.getInstance().getReference("User Data").child(userId)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.hasChildren()) {
                        // User has stored data, navigate to MainActivity
                        val intent = Intent(this@LoginUser, UserDashboard::class.java)
                        startActivity(intent)

                    } else {
                        // User doesn't have stored data or has empty data, navigate to AccountType
                        val intent = Intent(this@LoginUser, AccountType::class.java)
                        startActivity(intent)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error if needed
                    Log.e("CheckUserData", "Error accessing database: ${error.message}")
                    Toast.makeText(this@LoginUser, "Error accessing database", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}