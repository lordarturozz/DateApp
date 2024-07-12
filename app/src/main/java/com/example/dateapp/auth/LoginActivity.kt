package com.example.dateapp.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dateapp.MainActivity
import com.example.dateapp.R
import com.example.dateapp.databinding.ActivityLoginBinding
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private var verificationId: String? = null
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create the AlertDialog with the custom loading layout
        dialog = AlertDialog.Builder(this)
            .setView(R.layout.loading_layout)
            .setCancelable(false)  // Optional: prevent the dialog from being canceled
            .create()

        binding.sendOtp.setOnClickListener {
            val userNumber = binding.userNumber.text.toString()
            if (userNumber.isEmpty()) {
                binding.userNumber.error = "Por favor introduce tu número"
            } else {
                sendOtp(userNumber)
            }
        }

        binding.verifyOtp.setOnClickListener {
            val userOtp = binding.userOtp.text.toString()
            if (userOtp.isEmpty()) {
                binding.userOtp.error = "Por favor introduce tu OTP"
            } else {
                verifyOtp(userOtp)
            }
        }
    }

    private fun sendOtp(number: String) {
        dialog.show()
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                dialog.dismiss()
                // Optionally, handle auto-retrieved OTP scenario
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                dialog.dismiss()
                // Handle the error
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                this@LoginActivity.verificationId = verificationId
                dialog.dismiss()
                binding.numberLayout.visibility = View.GONE
                binding.otpLayout.visibility = View.VISIBLE
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Use the user's phone number
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOtp(otp: String) {
        dialog.show()
        val verificationId = verificationId ?: return
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    checkUserExist(binding.userNumber.text.toString())
                } else {
                    dialog.dismiss()
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserExist(number: String) {
        FirebaseDatabase.getInstance().getReference("users").child(number)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dialog.dismiss()
                    if (snapshot.exists()) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else {
                        startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                    }
                    finish()
                }

                override fun onCancelled(error: DatabaseError) {
                    dialog.dismiss()
                    Toast.makeText(this@LoginActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}





