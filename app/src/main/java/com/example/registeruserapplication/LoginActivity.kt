package com.example.registeruserapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.registeruserapplication.databinding.LoginMainBinding

class LoginActivity : AppCompatActivity() {

    lateinit var loginMainBinding: LoginMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginMainBinding = LoginMainBinding.inflate(layoutInflater)
        val view = loginMainBinding.root

        setContentView(view)

        loginMainBinding.buttonSingIn.setOnClickListener {

        }

        loginMainBinding.buttonSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}