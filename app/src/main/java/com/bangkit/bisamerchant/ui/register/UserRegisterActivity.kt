package com.bangkit.bisamerchant.ui.register

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityUserRegisterBinding
import com.bangkit.bisamerchant.services.Auth
import com.bangkit.bisamerchant.ui.termpolicy.PrivacyPolicyActivity
import com.bangkit.bisamerchant.ui.termpolicy.TermConditionActivity

class UserRegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityUserRegisterBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupClickListeners()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
            R.id.btn_register -> {
                val name = binding.tilRegistName.editText?.text.toString()
                val email = binding.tilRegistEmail.editText?.text.toString()
                val password = binding.tilRegistPassword.editText?.text.toString()
                val pin = binding.tilRegistPin.editText?.text.toString()
                if (name.isEmpty()) {
                    Toast.makeText(this, "Name is required!", Toast.LENGTH_SHORT).show()
                } else if (email.isEmpty()) {
                    Toast.makeText(this, "Email is required!", Toast.LENGTH_SHORT).show()
                } else if (password.isEmpty()) {
                    Toast.makeText(this, "Password is required!", Toast.LENGTH_SHORT).show()
                } else if (password.length < 6) {
                    Toast.makeText(this, "Password less than 6 digit!", Toast.LENGTH_SHORT).show()
                } else if (pin.isEmpty()) {
                    Toast.makeText(this, "PIN is required!", Toast.LENGTH_SHORT).show()
                } else if (pin.length < 6) {
                    Toast.makeText(this, "PIN less than 6 digit!", Toast.LENGTH_SHORT).show()
                } else {
                    Auth.register(this@UserRegisterActivity, name, email, password, pin)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.tvTerm.setOnClickListener(this)
        binding.tvPolicy.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
    }
}