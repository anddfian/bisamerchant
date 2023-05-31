package com.bangkit.bisamerchant.presentation.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityLoginBinding
import com.bangkit.bisamerchant.core.services.Auth
import com.bangkit.bisamerchant.presentation.register.UserRegisterActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupClickListeners()

        binding?.btnLogin?.isEnabled = false

        val textfields = listOf(
            binding?.tilLoginEmail?.editText,
            binding?.tilLoginPassword?.editText
        )

        textfields.forEach { it ->
            it?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val allFilled = textfields.all { it?.text?.isNotEmpty() ?: false }
                    binding?.btnLogin?.isEnabled = allFilled
                }
            })
        }
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
                val email = binding?.tilLoginEmail?.editText?.text.toString()
                val password = binding?.tilLoginPassword?.editText?.text.toString()
                when {
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        binding?.tilLoginEmail?.error = "Format email salah!"
                    }

                    password.length < 6 -> {
                        binding?.tilLoginPassword?.error = "Password kurang dari 6 digit!"
                    }

                    else -> {
                        Auth.login(this, this@LoginActivity, email, password)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupClickListeners() {
        binding?.tbForgotPassword?.setOnClickListener(this)
        binding?.tbCreateAccount?.setOnClickListener(this)
        binding?.btnLogin?.setOnClickListener(this)
    }
}