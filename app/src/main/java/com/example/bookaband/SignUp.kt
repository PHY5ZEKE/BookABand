package com.example.bookaband

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bookaband.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this,"Successfully Signed Up",Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, Landing::class.java)
                                startActivity(intent)
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
