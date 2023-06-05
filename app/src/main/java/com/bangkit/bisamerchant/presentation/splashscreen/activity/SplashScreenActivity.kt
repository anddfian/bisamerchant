package com.bangkit.bisamerchant.presentation.splashscreen.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bangkit.bisamerchant.databinding.ActivitySplashScreenBinding
import com.bangkit.bisamerchant.presentation.home.activity.HomeActivity
import com.bangkit.bisamerchant.presentation.onboarding.OnboardingActivity
import com.bangkit.bisamerchant.presentation.merchantregister.activity.MerchantRegisterActivity
import com.bangkit.bisamerchant.presentation.splashscreen.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding!!
    private val threeSecond = 3000L

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startNotificationService()
        startSplashScreen()
    }

    private fun startNotificationService() {
        if (Build.VERSION.SDK_INT >= 26) {
            this@SplashScreenActivity.startForegroundService(intent)
        } else {
            this@SplashScreenActivity.startService(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun startSplashScreen() {
        lifecycleScope.launch {
            delay(threeSecond)
            splashViewModel.getMerchantActive()
            splashViewModel.message.observe(this@SplashScreenActivity) { message ->
                when (message) {
                    MERCHANT_NOT_FOUND -> {
                        val intent = Intent(
                            this@SplashScreenActivity,
                            MerchantRegisterActivity::class.java
                        )
                        startActivity(intent)
                        finish()
                    }

                    OWNER_NOT_AUTHENTICATED -> {
                        val intent =
                            Intent(this@SplashScreenActivity, OnboardingActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    OWNER_AUTHENTICATED -> {
                        val intent = Intent(this@SplashScreenActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }

        }
    }

    companion object {
        private const val MERCHANT_NOT_FOUND = "Merchant not found"
        private const val OWNER_NOT_AUTHENTICATED = "Owner is not logged in"
        private const val OWNER_AUTHENTICATED = "Logged in"
    }
}