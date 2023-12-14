package com.example.bookaband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bookaband.databinding.ActivityLoginBinding
import com.example.bookaband.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
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
        // ...

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Email and password are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        checkUserDataAndNavigate()
                    } else {
                        val errorMessage = when ((task.exception as? FirebaseAuthException)?.errorCode) {
                            "ERROR_LOGIN_CREDENTIALS" ->
                                "Invalid credentials. Please try again."
                            "ERROR_USER_NOT_FOUND" ->
                                "No account found with this email address."
                            else ->
                                task.exception?.localizedMessage ?: "Authentication failed."
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }

// ...


    }

    private fun checkUserDataAndNavigate() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val userId = user.uid

            val userReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(userSnapshot: DataSnapshot) {
                    if (userSnapshot.exists()) {
                        // User data exists, check the "role" field
                        val role = userSnapshot.child("role").getValue(String::class.java)

                        if (role == "Band") {
                            // User has the role "Band", check Band Data
                            val bandReference = FirebaseDatabase.getInstance().getReference("Band Data").child(userId)
                            bandReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(bandSnapshot: DataSnapshot) {
                                    if (bandSnapshot.exists() && bandSnapshot.hasChildren()) {
                                        // User has stored data in "Band Data", navigate to BandDashboard
                                        val intent = Intent(this@Login, BandDashboard::class.java)
                                        startActivity(intent)
                                    } else {
                                        // User doesn't have stored data or has empty data in "Band Data", navigate to CreateBand
                                        val createUserIntent = Intent(this@Login, CreateBand::class.java)
                                        startActivity(createUserIntent)
                                    }
                                }

                                override fun onCancelled(bandError: DatabaseError) {
                                    // Handle Band Data database error if needed
                                    Log.e("CheckBandData", "Error accessing Band Data: ${bandError.message}")
                                    Toast.makeText(this@Login, "Error accessing Band Data", Toast.LENGTH_SHORT).show()
                                }
                            })
                        } else if(role == "User")
                        {
                            val bandReference = FirebaseDatabase.getInstance().getReference("User Data").child(userId)
                            bandReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(bandSnapshot: DataSnapshot) {
                                    if (bandSnapshot.exists() && bandSnapshot.hasChildren()) {
                                        // User has stored data in "Band Data", navigate to BandDashboard
                                        val intent = Intent(this@Login, UserDashboard::class.java)
                                        startActivity(intent)
                                    } else {
                                        // User doesn't have stored data or has empty data in "Band Data", navigate to CreateBand
                                        val createUserIntent = Intent(this@Login, CreateUser::class.java)
                                        startActivity(createUserIntent)
                                    }
                                }

                                override fun onCancelled(bandError: DatabaseError) {
                                    // Handle Band Data database error if needed
                                    Log.e("CheckBandData", "Error accessing Band Data: ${bandError.message}")
                                    Toast.makeText(this@Login, "Error accessing Band Data", Toast.LENGTH_SHORT).show()
                                }
                            })

                        }
                        else {
                            // User has a different role, handle accordingly
                            Toast.makeText(this@Login, "Invalid role for Band Dashboard", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // User data doesn't exist, handle accordingly
                        Toast.makeText(this@Login, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(userError: DatabaseError) {
                    // Handle user data database error if needed
                    Log.e("CheckUserData", "Error accessing user data: ${userError.message}")
                    Toast.makeText(this@Login, "Error accessing user data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


}