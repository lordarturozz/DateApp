package com.example.dateapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dateapp.Greeting
import com.example.dateapp.MainActivity
import com.example.dateapp.R
import com.example.dateapp.auth.LoginActivity
import com.example.dateapp.ui.theme.DateAppTheme
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


                    val user = FirebaseAuth.getInstance().currentUser
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (user == null)
                            startActivity(Intent(this, LoginActivity::class.java))
                        else
                            startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }, 2000)
                }
            }



