package com.example.registeruserapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.registeruserapplication.databinding.ActivityUserBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.UUID

class UserActivity : AppCompatActivity() {
    lateinit var addUserBinding: ActivityUserBinding

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef: DatabaseReference = database.reference.child("MyUsers")

    var imageUri: Uri? = null

    val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference: StorageReference = firebaseStorage.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addUserBinding = ActivityUserBinding.inflate(layoutInflater)
        val view = addUserBinding.root
        setContentView(view)

        supportActionBar?.title = "Create User"

        // register
        registerActivityForResults()

        addUserBinding.buttonAddUser.setOnClickListener {
            uploadPhoto()
        }

        addUserBinding.imageViewUserProfile.setOnClickListener {
            selectImage()
        }
    }

    fun selectImage() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            println("Error >>>>>>>")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.READ_MEDIA_IMAGES),
                1
            )
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        } else {

        }
    }

    fun registerActivityForResults() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->

                val resultCode = result.resultCode
                val imageData = result.data

                if (resultCode == RESULT_OK && imageData != null) {
                    imageUri = imageData.data

                    // Picasso
                    imageUri?.let {
                        it
                        Picasso.get()
                            .load(it)
                            .into(addUserBinding.imageViewUserProfile)
                    }

                }
            })
    }

    private fun addUserToDb(url: String) {
        val name: String = addUserBinding.editTextName.text.toString()
        val age: Int = addUserBinding.editTextAge.text.toString().toInt()
        val email: String = addUserBinding.editTextEmailAddress.text.toString()

        val id = myRef.push().key.toString()

        val user = Users(id, name, age, email, url)

        myRef.child(id).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Add user successfully", Toast.LENGTH_SHORT)
                    .show()

                addUserBinding.buttonAddUser.isClickable = true
                addUserBinding.progressBar.isVisible = false

                finish()
            } else {
                Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_SHORT)
                    .show()

            }
        }
    }

    fun uploadPhoto() {
        addUserBinding.buttonAddUser.isClickable = false
        addUserBinding.progressBar.isVisible = true

        val imageName = UUID.randomUUID().toString()

        val imageReference = storageReference.child("images").child(imageName)

        imageUri?.let {
            imageReference.putFile(it).addOnSuccessListener {
                Toast.makeText(applicationContext, "Image uploaded", Toast.LENGTH_SHORT).show()

                // downloadable url

                val myUploadedImageReference = storageReference.child("images").child(imageName)

                myUploadedImageReference.downloadUrl.addOnSuccessListener { url ->
                    val imageURL = url.toString()
                    addUserToDb(imageURL)
                }

            }.addOnFailureListener { it ->
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}