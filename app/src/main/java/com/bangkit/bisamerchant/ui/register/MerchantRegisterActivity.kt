package com.bangkit.bisamerchant.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.bisamerchant.databinding.ActivityMerchantRegisterBinding

class MerchantRegisterActivity : AppCompatActivity() {
    private var _binding: ActivityMerchantRegisterBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMerchantRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}