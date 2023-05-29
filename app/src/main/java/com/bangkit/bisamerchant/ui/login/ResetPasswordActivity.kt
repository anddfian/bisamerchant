package com.bangkit.bisamerchant.ui.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityResetPasswordBinding
import com.bangkit.bisamerchant.core.services.Auth

class ResetPasswordActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityResetPasswordBinding? = null
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding?.root)

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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_reset_password -> {
                val email = binding?.tilLoginEmail?.editText?.text.toString()
                when {
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        binding?.tilLoginEmail?.error = "Format email salah!"
                    }

                    else -> {
                        Auth.resetPasswordEmail(this@ResetPasswordActivity, email)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}