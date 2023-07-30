package com.example.registeruserapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.registeruserapplication.databinding.ActivityUserUpdateBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.UUID

class UpdateUserActivity : AppCompatActivity() {
    lateinit var userUpdateBinding: ActivityUserUpdateBinding

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference = database.reference.child("MyUsers")

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    var imageUri: Uri? = null

    val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference: StorageReference = firebaseStorage.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userUpdateBinding = ActivityUserUpdateBinding.inflate(layoutInflater)
        val view = userUpdateBinding.root
        setContentView(view)

        getAndSetData()

        // register
        registerActivityForResults()

        userUpdateBinding.buttonUpdateUser.setOnClickListener {
            uploadPhoto()
        }

        userUpdateBinding.imageViewUpdateUserProfile.setOnClickListener {
            selectImage()
        }
    }

    fun selectImage() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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
                            .into(userUpdateBinding.imageViewUpdateUserProfile)
                    }

                }
            })
    }

    fun getAndSetData() {
        val name = intent.getStringExtra("name")
        val age = intent.getIntExtra("age", 0).toString()
        val email = intent.getStringExtra("email")
        val url = intent.getStringExtra("url")


        userUpdateBinding.editTextUpdateName.setText(name)
        userUpdateBinding.editTextUpdateAge.setText(age)
        userUpdateBinding.editTextUpdateEmailAddress.setText(email)

        Picasso.get().load(url).into(userUpdateBinding.imageViewUpdateUserProfile)
    }

    fun updateUser(imageURL: String, imageName: String) {
        val id = intent.getStringExtra("id").toString()

        val name = userUpdateBinding.editTextUpdateName.text.toString()
        val age = userUpdateBinding.editTextUpdateAge.text.toString().toInt()
        val email = userUpdateBinding.editTextUpdateEmailAddress.text.toString()

        val userMap = mutableMapOf<String, Any>()
        userMap["userId"] = id
        userMap["userName"] = name
        userMap["userAge"] = age
        userMap["userEmail"] = email
        userMap["imageName"] = imageName
        userMap["url"] = imageURL

        ref.child(id).updateChildren(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "User Updated Successfully", Toast.LENGTH_SHORT).show()

                userUpdateBinding.buttonUpdateUser.isClickable = true
                userUpdateBinding.updateProgressBar.isVisible = false

                finish()
            }else {
                Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun uploadPhoto() {
        userUpdateBinding.buttonUpdateUser.isClickable = false
        userUpdateBinding.updateProgressBar.isVisible = true

        val imageName = intent.getStringExtra("imageName").toString()

        if (imageName != null) {
            val imageReference = storageReference.child("images").child(imageName)
            imageUri?.let {
                imageReference.putFile(it).addOnSuccessListener {
                    Toast.makeText(applicationContext, "Image updated", Toast.LENGTH_SHORT).show()

                    // downloadable url

                    val myUploadedImageReference = storageReference.child("images").child(imageName)

                    myUploadedImageReference.downloadUrl.addOnSuccessListener { url ->
                        val imageURL = url.toString()
                            updateUser(imageURL, imageName)
                    }

                }.addOnFailureListener { it ->
                    Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}