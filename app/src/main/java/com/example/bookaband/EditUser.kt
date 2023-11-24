package com.example.bookaband

import android.app.AlertDialog
import android.content.ClipDescription
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class EditUser : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    private lateinit var uploadName: EditText
    private lateinit var uploadContact: EditText
    private lateinit var uploadUserDescription: EditText
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
        setContentView(R.layout.activity_edit_user)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("User Data")
        storageReference = FirebaseStorage.getInstance().reference

        uploadName = findViewById(R.id.uploadName)
        uploadContact = findViewById(R.id.uploadContact)
        uploadUserDescription = findViewById(R.id.uploadUserDescription)
        uploadImage = findViewById(R.id.uploadPfp)
        uploadButton = findViewById(R.id.saveUser)

        populateUI()

        // Set an onClickListener for your "Save" button
        uploadButton.setOnClickListener {
            // Call a method to update the data in Firebase
            updateData()
        }

        // Set an onClickListener for the image upload
        uploadImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_GET_CONTENT)
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
                    val data = snapshot.getValue(User::class.java)
                    if (data != null) {
                        uploadName.setText(data.name)
                        uploadContact.setText(data.contact)
                        uploadUserDescription.setText(data.desc)



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

        val storageReference = FirebaseStorage.getInstance().reference.child("User Pfp")
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
            val name = uploadName.text.toString().trim()
            val contact = uploadContact.text.toString().trim()
            val desc = uploadUserDescription.text.toString().trim()


            if (name.isEmpty() || contact.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
                return
            }

            try {

                val userUid = user.uid
                val userData = User(userUid, name, contact, desc,  imageURL)

                // Use updateChildren instead of setValue to update only specific fields
                val updateDataMap = mutableMapOf<String, Any>()
                updateDataMap["name"] = userData.name
                updateDataMap["contact"] = userData.contact
                updateDataMap["desc"] = userData.desc

                if (imageUri != null) {
                    updateDataMap["imageURL"] = userData.imageURL.toString()
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
