package com.example.bookaband

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.storage.FirebaseStorage
import android.app.AlertDialog;
import android.icu.text.DateFormat
import android.icu.util.Calendar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateBand : AppCompatActivity() {
    private lateinit var uploadImage: ImageView
    private lateinit var saveButton: Button
    private lateinit var uploadBandName: EditText
    private lateinit var uploadEmail: EditText
    private lateinit var uploadGenre: EditText
    private lateinit var uploadDescription: EditText
    private lateinit var uploadPriceRange: EditText
    private var imageURL: String? = null
    private var uri: Uri? = null
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_band)

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        uploadImage = findViewById(R.id.uploadImage)
        saveButton = findViewById(R.id.saveButton)
        uploadBandName = findViewById(R.id.uploadBandName)
        uploadEmail = findViewById(R.id.uploadEmail)
        uploadGenre = findViewById(R.id.uploadGenre)
        uploadDescription = findViewById(R.id.uploadDescription)
        uploadPriceRange = findViewById(R.id.uploadPriceRange)

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                uri = data?.data
                uploadImage.setImageURI(uri)
            } else {
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }

        uploadImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        saveButton.setOnClickListener {
            saveData()
        }
    }
    fun saveData() {
        if (uri == null) {
            Toast.makeText(this, "Image is required.", Toast.LENGTH_SHORT).show()
            return
        }

        val storageReference = FirebaseStorage.getInstance().reference.child("Band Images")
            .child(uri?.lastPathSegment.toString())
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        storageReference.putFile(uri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isComplete);
                val urlImage = uriTask.result
                imageURL = urlImage.toString()
                uploadData()
                dialog.dismiss()
            }
            .addOnFailureListener { e ->
                dialog.dismiss()
                Toast.makeText(this, "Failed to upload image.", Toast.LENGTH_SHORT).show()
            }
    }

    fun uploadData() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val name = uploadBandName.text.toString().trim()
            val email = uploadEmail.text.toString().trim()
            val genre = uploadGenre.text.toString().trim()
            val desc = uploadDescription.text.toString().trim()
            val price = uploadPriceRange.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || genre.isEmpty() || desc.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
                return
            }

            try {
                val priceFloat = price.toFloat()
                val userUid = user.uid
                val bandData = BandData(userUid, name, email, genre, desc, priceFloat, imageURL)


                FirebaseDatabase.getInstance().getReference("Band Data").child(userUid)
                    .setValue(bandData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid price format.", Toast.LENGTH_SHORT).show()
            }
        }
    }





}
