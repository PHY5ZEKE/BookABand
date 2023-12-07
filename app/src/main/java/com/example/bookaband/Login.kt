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
            val databaseReference = FirebaseDatabase.getInstance().getReference("Band Data").child(userId)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.hasChildren()) {
                        // User has stored data, navigate to UserDashboard


                        val intent = Intent(this@Login, BandDashboard::class.java)
                        startActivity(intent)

                    } else {
                        // User doesn't have stored data or has empty data, navigate to CreateUser
                        // Display toast if the reference data is not under "Band Data"
                        val bandReference = FirebaseDatabase.getInstance().getReference("User Data").child(userId)
                        bandReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                if (userSnapshot.exists() || userSnapshot.hasChildren()) {
                                    val landingIntent = Intent(this@Login, LoginUser::class.java)
                                    startActivity(landingIntent)
                                    Toast.makeText(this@Login, "This is not a band account", Toast.LENGTH_SHORT).show()
                                } else {
                                    // User exists but data is empty or missing, navigate to CreateUser
                                    val createUserIntent = Intent(this@Login, CreateBand::class.java)
                                    startActivity(createUserIntent)
                                }
                            }

                            override fun onCancelled(bandError: DatabaseError) {
                                // Handle band data database error if needed
                                Log.e("CheckBandData", "Error accessing band data: ${bandError.message}")
                                Toast.makeText(this@Login, "Error accessing band data", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle user data database error if needed
                    Log.e("CheckUserData", "Error accessing user data: ${error.message}")
                    Toast.makeText(this@Login, "Error accessing user data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}
