package com.example.registeruserapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.registeruserapplication.databinding.ActivityUserBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserActivity : AppCompatActivity() {
    lateinit var addUserBinding: ActivityUserBinding

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef: DatabaseReference = database.reference.child("MyUsers")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addUserBinding = ActivityUserBinding.inflate(layoutInflater)
        val view = addUserBinding.root
        setContentView(view)

        supportActionBar?.title = "Create User"

        addUserBinding.buttonAddUser.setOnClickListener {
            addUserToDb()
        }
    }

    private fun addUserToDb() {
        val name: String = addUserBinding.editTextName.text.toString()
        val age: Int = addUserBinding.editTextAge.text.toString().toInt()
        val email: String = addUserBinding.editTextEmailAddress.text.toString()

        val id = myRef.push().key.toString()

        val user = Users(id, name, age, email)

        myRef.child(id).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(applicationContext, "Add user successfully", Toast.LENGTH_SHORT).show()

                finish()
            }else {
                Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_SHORT).show()

            }
        }
    }
}