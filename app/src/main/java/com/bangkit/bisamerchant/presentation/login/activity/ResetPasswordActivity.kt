package com.bangkit.bisamerchant.presentation.login.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityResetPasswordBinding
import com.bangkit.bisamerchant.presentation.login.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityResetPasswordBinding? = null
    private val binding get() = _binding

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        updateUI()


        binding.apply {
            this?.btnResetPassword?.setOnClickListener(this@ResetPasswordActivity).also {
                this?.btnResetPassword?.isEnabled = false
            }
        }

        val textfield = binding?.tilLoginEmail?.editText

        textfield?.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filled = textfield.text?.isNotEmpty() ?: false
                binding?.btnResetPassword?.isEnabled = filled
            }
        })
    }

    private fun updateUI() {
        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        loginViewModel.message.observe(this) {
            if (it != null) {
                Toast.makeText(this@ResetPasswordActivity, it, Toast.LENGTH_SHORT)
                    .show()
            }
            if (it == RESET_SUCCESSFUL) {
                val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_reset_password -> {
                val email = binding?.tilLoginEmail?.editText?.text.toString()
                when {
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        binding?.tilLoginEmail?.error = "Format email salah!"
                    }

                    else -> {
                        loginViewModel.resetPassword(email)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val RESET_SUCCESSFUL = "Reset password sent, check your email"
    }
}