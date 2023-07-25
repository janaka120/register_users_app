package com.example.registeruserapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.registeruserapplication.databinding.ActivityForgetBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class ForgetActivity : AppCompatActivity() {
    lateinit var forgetBinding: ActivityForgetBinding

    val authFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgetBinding = ActivityForgetBinding.inflate(layoutInflater)
        val view = forgetBinding.root
        setContentView(view)

        forgetBinding.buttonReset.setOnClickListener {
        val email = forgetBinding.editTextResetEmailAddress.text.toString()
            authFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Password reset email send. Please check", Toast.LENGTH_SHORT).show()

                    finish()
                }else {
                    Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}