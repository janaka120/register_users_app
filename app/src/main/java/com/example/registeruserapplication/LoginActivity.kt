package com.example.registeruserapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.registeruserapplication.databinding.LoginMainBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var loginMainBinding: LoginMainBinding
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginMainBinding = LoginMainBinding.inflate(layoutInflater)
        val view = loginMainBinding.root

        setContentView(view)

        loginMainBinding.buttonSingIn.setOnClickListener {
            val email = loginMainBinding.editTextEmailSignIn.text.toString()
            val userPassword = loginMainBinding.editTextPasswordSignIn.text.toString()

            signInWithFirebase(email, userPassword)
        }

        loginMainBinding.buttonSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        loginMainBinding.buttonForgetPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgetActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signInWithFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Login successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else {
                Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser

        if (user != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}