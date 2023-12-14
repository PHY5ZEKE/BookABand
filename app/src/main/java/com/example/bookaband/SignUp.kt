package com.example.bookaband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.bookaband.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, Landing::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            // Get the selected value from the spinner
            val selectedRole = binding.Access.selectedItem.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && selectedRole.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Get the user ID after successful registration
                                val userId = firebaseAuth.currentUser?.uid

                                // Create a reference to the user's data in the database
                                val userRef = firebaseDatabase.reference.child("users").child(userId ?: "")

                                // Store the selected role in the "role" field
                                userRef.child("role").setValue(selectedRole)

                                Toast.makeText(this, "Successfully Signed Up", Toast.LENGTH_SHORT).show()

                                // Redirect based on the selected role
                                if (selectedRole == "Band") {
                                    val intent = Intent(this, Login::class.java)
                                    startActivity(intent)
                                }
//                                else if (selectedRole == "User") {
//                                    val intent = Intent(this, LoginUser::class.java)
//                                    startActivity(intent)
//                                }
                            } else {
                                handleFirebaseException(task.exception)
                            }
                        }
                } else {
                    Toast.makeText(this, "Password does not match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields are empty!", Toast.LENGTH_SHORT).show()
            }
        }
        // Define your spinner
        val spinner: Spinner = findViewById(R.id.Access)

// Prepare data (list of strings)
        val data = listOf("", "Band", "User")

// Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// Apply the adapter to the spinner
        spinner.adapter = adapter

    }

    private fun handleFirebaseException(exception: Exception?) {
        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                // Handle weak password exception
                Toast.makeText(
                    this,
                    "Weak password. Choose a stronger password.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                // Handle invalid email exception
                Toast.makeText(this, "Invalid email address.", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthUserCollisionException -> {
                // Handle user collision exception
                Toast.makeText(this, "Email address is already in use.", Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                // Handle other exceptions
                Toast.makeText(
                    this,
                    "Authentication failed: ${exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}