package com.example.registeruserapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.registeruserapplication.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    lateinit var signUpBinding: ActivitySignUpBinding
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = signUpBinding.root

        setContentView(view)

        signUpBinding.buttonSignUp.setOnClickListener {
            val email = signUpBinding.editTextEmailSignUp.text.toString()
            val userPassword = signUpBinding.editTextPasswordSignUp.text.toString()

            signupWithFirebase(email, userPassword)
        }
    }

    fun signupWithFirebase(userEmail: String, userPassword: String) {
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Your account has been created", Toast.LENGTH_SHORT).show()
                finish()

            }else {
                println(">>>>>> " + task.exception.toString())
                Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}