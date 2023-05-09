package com.bangkit.bisamerchant.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityUserRegisterBinding
import com.bangkit.bisamerchant.ui.termpolicy.PrivacyPolicyActivity
import com.bangkit.bisamerchant.ui.termpolicy.TermConditionActivity

class UserRegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityUserRegisterBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTerm.setOnClickListener(this)
        binding.tvPolicy.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        binding.btnCreateAccount.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.tv_term -> {
                val intent = Intent(this@UserRegisterActivity, TermConditionActivity::class.java)
                startActivity(intent)
            }
            R.id.tv_policy -> {
                val intent = Intent(this@UserRegisterActivity, PrivacyPolicyActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_login -> {
                finish()
            }
            R.id.btn_create_account -> {
                val intent = Intent(this@UserRegisterActivity, MerchantRegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}