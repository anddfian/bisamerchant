package com.bangkit.bisamerchant.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivitySettingBinding
import com.bangkit.bisamerchant.services.Auth
import com.bangkit.bisamerchant.ui.faq.FaqActivity
import com.bangkit.bisamerchant.ui.termpolicy.PrivacyPolicyActivity
import com.bangkit.bisamerchant.ui.termpolicy.TermConditionActivity

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
            R.id.cv_setting1 -> {
                startActivity(Intent(this@SettingActivity, MerchantSettingActivity::class.java))
            }

            R.id.cv_setting2 -> {
                startActivity(Intent(this@SettingActivity, TermConditionActivity::class.java))
            }

            R.id.cv_setting3 -> {
                startActivity(Intent(this@SettingActivity, PrivacyPolicyActivity::class.java))
            }

            R.id.cv_setting4 -> {
                startActivity(Intent(this@SettingActivity, FaqActivity::class.java))
            }

            R.id.cv_setting5 -> {
                startActivity(Intent(this@SettingActivity, FaqActivity::class.java))
            }

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
        binding.cvSetting1.setOnClickListener(this)
        binding.cvSetting2.setOnClickListener(this)
        binding.cvSetting3.setOnClickListener(this)
        binding.cvSetting4.setOnClickListener(this)
        binding.cvSetting5.setOnClickListener(this)
    }
}