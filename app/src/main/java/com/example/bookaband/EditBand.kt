package com.example.bookaband

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class EditBand : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    private lateinit var uploadBandName: EditText
    private lateinit var uploadEmail: EditText
    private lateinit var uploadGenre: EditText
    private lateinit var uploadDescription: EditText
    private lateinit var uploadPriceRange: EditText
    private lateinit var uploadImage: ImageView
    private lateinit var uploadButton: Button

    private var imageUri: Uri? = null
    private var imageURL: String = "" // Assuming imageURL is declared somewhere

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                imageUri = uri
                uploadImage.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_band)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Band Data")
        storageReference = FirebaseStorage.getInstance().reference

        uploadBandName = findViewById(R.id.uploadBandName)
        uploadEmail = findViewById(R.id.uploadEmail)
        uploadGenre = findViewById(R.id.uploadGenre)
        uploadDescription = findViewById(R.id.uploadDescription)
        uploadPriceRange = findViewById(R.id.uploadPriceRange)
        uploadImage = findViewById(R.id.uploadImage)
        uploadButton = findViewById(R.id.saveButton)

        populateUI()

        // Set an onClickListener for your "Save" button
        uploadButton.setOnClickListener {
            // Call a method to update the data in Firebase
            updateData()
        }

        // Set an onClickListener for the image upload
        uploadImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }
    }

    private fun populateUI() {
        // Get the current user's UID
        val uid = auth.currentUser!!.uid

        // Query Firebase to get the data for the current user
        databaseReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Retrieve data and set it to the UI elements
                    val data = snapshot.getValue(BandData::class.java)
                    if (data != null) {
                        uploadBandName.setText(data.name)
                        uploadEmail.setText(data.email)
                        uploadGenre.setText(data.genre)
                        uploadDescription.setText(data.desc)
                        uploadPriceRange.setText(data.price.toString())

                        // Load image using Glide or Picasso or other image loading libraries
                        // Example using Glide:
                        // Glide.with(this@EditBand).load(data.imageURL).into(uploadImage)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun updateData() {
        if (imageUri == null) {
            // If no new image is selected, directly update text data
            uploadTextData()
            return
        }

        val storageReference = FirebaseStorage.getInstance().reference.child("Band Images")
            .child(imageUri?.lastPathSegment.toString())
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        storageReference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isComplete);
                val urlImage = uriTask.result
                imageURL = urlImage.toString()
                uploadTextData() // After uploading image, update text data
                dialog.dismiss()
            }
            .addOnFailureListener { e ->
                dialog.dismiss()
                Toast.makeText(this, "Failed to upload image.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadTextData() {
        val currentUser = auth.currentUser
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

                // Use updateChildren instead of setValue to update only specific fields
                val updateDataMap = mutableMapOf<String, Any>()
                updateDataMap["name"] = bandData.name
                updateDataMap["email"] = bandData.email
                updateDataMap["genre"] = bandData.genre
                updateDataMap["desc"] = bandData.desc
                updateDataMap["price"] = bandData.price
                if (imageUri != null) {
                    updateDataMap["imageURL"] = bandData.imageURL.toString()
                }

                databaseReference.child(userUid)
                    .updateChildren(updateDataMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to update data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid price format.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
