package com.example.registeruserapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.registeruserapplication.databinding.ActivityUserUpdateBinding

class UpdateUserActivity : AppCompatActivity() {
    lateinit var userUpdateBinding: ActivityUserUpdateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userUpdateBinding = ActivityUserUpdateBinding.inflate(layoutInflater)
        val view = userUpdateBinding.root
        setContentView(view)
    }
}