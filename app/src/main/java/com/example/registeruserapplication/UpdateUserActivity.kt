package com.example.registeruserapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.registeruserapplication.databinding.ActivityUserUpdateBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateUserActivity : AppCompatActivity() {
    lateinit var userUpdateBinding: ActivityUserUpdateBinding

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference = database.reference.child("MyUsers")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userUpdateBinding = ActivityUserUpdateBinding.inflate(layoutInflater)
        val view = userUpdateBinding.root
        setContentView(view)

        getAndSetData()

        userUpdateBinding.buttonUpdateUser.setOnClickListener {
            updateUser()
        }
    }

    fun getAndSetData() {
        val name = intent.getStringExtra("name")
        val age = intent.getIntExtra("age", 0).toString()
        val email = intent.getStringExtra("email")

        userUpdateBinding.editTextUpdateName.setText(name)
        userUpdateBinding.editTextUpdateAge.setText(age)
        userUpdateBinding.editTextUpdateEmailAddress.setText(email)
    }

    fun updateUser() {
        val id = intent.getStringExtra("id").toString()

        val name = userUpdateBinding.editTextUpdateName.text.toString()
        val age = userUpdateBinding.editTextUpdateAge.text.toString().toInt()
        val email = userUpdateBinding.editTextUpdateEmailAddress.text.toString()

        val userMap = mutableMapOf<String, Any>()
        userMap["userId"] = id
        userMap["userName"] = name
        userMap["userAge"] = age
        userMap["userEmail"] = email

        ref.child(id).updateChildren(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "User Updated Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }else {
                Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}