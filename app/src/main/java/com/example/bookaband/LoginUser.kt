package com.example.bookaband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bookaband.databinding.ActivityLoginUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
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

        // ...

        binding.signInUser.setOnClickListener {
            val email = binding.emailEtUser.text.toString()
            val pass = binding.passETUser.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Email and password are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkUserDataAndNavigate()
                } else {
                    val errorMessage =
                        when ((task.exception as? FirebaseAuthException)?.errorCode) {
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

        }
    }

// ...




    private fun checkUserDataAndNavigate() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val databaseReference = FirebaseDatabase.getInstance().getReference("User Data").child(userId)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.hasChildren()) {
                        // User has stored data, navigate to UserDashboard
                        val intent = Intent(this@LoginUser, UserDashboard::class.java)
                        startActivity(intent)
                    } else {
                        // User doesn't have stored data or has empty data, navigate to CreateUser
                        // Display toast if the reference data is not under "Band Data"
                        val bandReference = FirebaseDatabase.getInstance().getReference("Band Data").child(userId)
                        bandReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(bandSnapshot: DataSnapshot) {
                                if (bandSnapshot.exists() || bandSnapshot.hasChildren()) {
                                    val landingIntent = Intent(this@LoginUser, Landing::class.java)
                                    startActivity(landingIntent)
                                    Toast.makeText(this@LoginUser, "This is not a user account", Toast.LENGTH_SHORT).show()
                                } else {
                                    // User exists but data is empty or missing, navigate to CreateUser
                                    val createUserIntent = Intent(this@LoginUser, CreateUser::class.java)
                                    startActivity(createUserIntent)
                                }
                            }

                            override fun onCancelled(bandError: DatabaseError) {
                                // Handle band data database error if needed
                                Log.e("CheckBandData", "Error accessing band data: ${bandError.message}")
                                Toast.makeText(this@LoginUser, "Error accessing band data", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle user data database error if needed
                    Log.e("CheckUserData", "Error accessing user data: ${error.message}")
                    Toast.makeText(this@LoginUser, "Error accessing user data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}
