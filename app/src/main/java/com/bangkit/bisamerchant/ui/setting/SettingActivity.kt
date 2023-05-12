package com.bangkit.bisamerchant.ui.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivitySettingBinding
import com.bangkit.bisamerchant.services.Auth

class SettingActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivitySettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_logout -> {
                Auth.logout(this, this@SettingActivity)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener(this)
    }}