package com.example.dateapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dateapp.MainActivity
import com.example.dateapp.R
import com.example.dateapp.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Check the current user status
        val user = FirebaseAuth.getInstance().currentUser

        // Use coroutine for the delay
        lifecycleScope.launch {
            delay(2000L) // Delay for 2 seconds
            val nextActivity = if (user == null) {
                LoginActivity::class.java
            } else {
                MainActivity::class.java
            }
            startActivity(Intent(this@SplashScreenActivity, nextActivity))
            finish()
        }
    }
}


