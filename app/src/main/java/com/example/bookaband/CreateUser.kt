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

class CreateUser : AppCompatActivity() {
    private lateinit var uploadPfp: ImageView
    private lateinit var saveUser: Button
    private lateinit var uploadName: EditText
    private lateinit var uploadContact: EditText
    private lateinit var uploadUserDescription: EditText
    private var imageURL: String? = null
    private var uri: Uri? = null
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        uploadPfp = findViewById(R.id.uploadPfp)
        saveUser = findViewById(R.id.saveUser)
        uploadName = findViewById(R.id.uploadName)
        uploadContact = findViewById(R.id.uploadContact)
        uploadUserDescription = findViewById(R.id.uploadUserDescription)


        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                uri = data?.data
                uploadPfp.setImageURI(uri)
            } else {
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }

        uploadPfp.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        saveUser.setOnClickListener {
            saveData()
        }
    }

    fun saveData() {
        if (uri == null) {
            Toast.makeText(this, "Image is required.", Toast.LENGTH_SHORT).show()
            return
        }

        val storageReference = FirebaseStorage.getInstance().reference.child("User Pfp")
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
            val name = uploadName.text.toString().trim()
            val contact = uploadContact.text.toString().trim()
            val desc = uploadUserDescription.text.toString().trim()

            if (name.isEmpty() || contact.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
                return
            }

            val userUid = user.uid
            val userData = User(userUid, name, contact, desc, imageURL)

            val currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
            FirebaseDatabase.getInstance().getReference("User Data").child(userUid)
                .child(currentDate)
                .setValue(userData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
