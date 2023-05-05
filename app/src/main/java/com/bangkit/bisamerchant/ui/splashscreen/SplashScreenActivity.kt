package com.bangkit.bisamerchant.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.ui.onboarding.OnboardingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        CoroutineScope(Dispatchers.Default).launch {
            delay(THREE_SECOND)
            val intent = Intent(this@SplashScreenActivity, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val THREE_SECOND = 3000L
    }
}