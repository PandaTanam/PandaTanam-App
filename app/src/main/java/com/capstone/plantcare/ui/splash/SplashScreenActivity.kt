package com.capstone.plantcare.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.plantcare.R
import com.capstone.plantcare.ui.login.LoginActivity
import com.capstone.plantcare.ui.onboarding.OnBoardingActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupSplashScreen()
    }

    private fun setupSplashScreen() {
        val sharedPref = getSharedPreferences("onboarding", MODE_PRIVATE)
        val isOnboardingCompleted = sharedPref.getBoolean("isOnBoardingCompleted", false)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (isOnboardingCompleted) {
                Intent(this, LoginActivity::class.java)
            } else {
                with(sharedPref.edit()) {
                    putBoolean("isOnBoardingCompleted", true)
                    apply()
                }
                Intent(this, OnBoardingActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
}