package com.bangkit.bisamerchant.presentation.login.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityLoginBinding
import com.bangkit.bisamerchant.presentation.login.viewmodel.LoginViewModel
import com.bangkit.bisamerchant.presentation.merchantregister.MerchantRegisterActivity
import com.bangkit.bisamerchant.presentation.register.activity.RegisterActivity
import com.bangkit.bisamerchant.presentation.resetpassword.ResetPasswordActivity
import com.bangkit.bisamerchant.presentation.splashscreen.activity.SplashScreenActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupClickListeners()
        setupCustomButton()
        updateUI()
    }

    private fun updateUI() {
        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        loginViewModel.message.observe(this) {
            Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
        }
        loginViewModel.message.observe(this) {
            if (loginViewModel.message.value == LOGIN_SUCCESSFUL) {
                val intent =
                    Intent(this, SplashScreenActivity::class.java)
                startActivity(intent)
                finish()
            } else if (loginViewModel.message.value == MERCHANT_NOT_FOUND){
                val intent = Intent(
                    this,
                    MerchantRegisterActivity::class.java
                )
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupCustomButton() {
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
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
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
                        loginViewModel.login(email, password)
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

    companion object {
        private const val MERCHANT_NOT_FOUND = "Merchant not found"
        private const val LOGIN_SUCCESSFUL = "Login successful"
    }
}