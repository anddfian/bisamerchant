package com.bangkit.bisamerchant.presentation.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bisamerchant.databinding.ActivitySplashScreenBinding
import com.bangkit.bisamerchant.core.services.Auth
import com.bangkit.bisamerchant.core.services.Merchant
import com.bangkit.bisamerchant.presentation.home.activity.HomeActivity
import com.bangkit.bisamerchant.presentation.onboarding.OnboardingActivity
import com.bangkit.bisamerchant.presentation.merchantregister.MerchantRegisterActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding!!
    private val threeSecond = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startSplashScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun startSplashScreen() {
        val isLogged = Auth.isLogged()
        if (isLogged) {
            CoroutineScope(Dispatchers.Default).launch {
                delay(threeSecond)

                Merchant.checkMerchantExists(
                    onSuccess = { exists ->
                        if (exists) {
                            val intent = Intent(this@SplashScreenActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(
                                this@SplashScreenActivity,
                                MerchantRegisterActivity::class.java
                            )
                            startActivity(intent)
                            finish()
                        }
                    },
                    onFailure = { exception ->
                        Toast.makeText(
                            this@SplashScreenActivity,
                            exception.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        } else {
            val intent = Intent(this@SplashScreenActivity, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}