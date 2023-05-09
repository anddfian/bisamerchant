package com.bangkit.bisamerchant.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityLoginBinding
import com.bangkit.bisamerchant.services.Auth
import com.bangkit.bisamerchant.ui.register.UserRegisterActivity

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tbForgotPassword.setOnClickListener(this)
        binding.tbCreateAccount.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tb_forgot_password -> {
                val intent = Intent(this@LoginActivity, ResetPasswordActivity::class.java)
                startActivity(intent)
            }
            R.id.tb_create_account -> {
                val intent = Intent(this@LoginActivity, UserRegisterActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_login -> {
                val email = binding.tilLoginEmail.editText?.text.toString()
                val password = binding.tilLoginPassword.editText?.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    Auth.login(this, this@LoginActivity, email, password)
                } else {
                    Toast.makeText(this, "Email dan Password harap diisi!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}