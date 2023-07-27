package com.example.registeruserapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.registeruserapplication.databinding.ActivitySignInPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class SignInPhoneActivity : AppCompatActivity() {

    lateinit var signInPhoneBinding: ActivitySignInPhoneBinding

    lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    var verificationCode = ""

    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInPhoneBinding = ActivitySignInPhoneBinding.inflate(layoutInflater)
        val view = signInPhoneBinding.root
        setContentView(view)

        signInPhoneBinding.buttonSignInPhoneSmsSend.setOnClickListener {
            val phone = signInPhoneBinding.editTextSignInPhoneNumber.text.toString()

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this@SignInPhoneActivity)
                .setCallbacks(mCallback)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }

        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            override fun onCodeSent(code: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(code, p1)

                verificationCode = code
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(p0: FirebaseException) {
                println("Exception 00 >>>>$p0")
            }
        }

        signInPhoneBinding.buttonSignInPhoneVerify.setOnClickListener {
            verifyWithSmsCode()
        }
    }

    fun verifyWithSmsCode() {
        val userCode = signInPhoneBinding.editTextSmsCodeSignInPhoneCode.text.toString()

        val credential = PhoneAuthProvider.getCredential(verificationCode, userCode)

        signWithPhoneAuthCredential(credential)
    }

    fun signWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@SignInPhoneActivity, "Successfully verify", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this@SignInPhoneActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                println("Exception >>>>" + task.exception.toString())
                Toast.makeText(this@SignInPhoneActivity, task.exception.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}