package com.bangkit.bisamerchant.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityResetPasswordBinding
import com.bangkit.bisamerchant.services.Auth

class ResetPasswordActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityResetPasswordBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnResetPassword.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_reset_password -> {
                val email = binding.tilLoginEmail.editText?.text.toString()
                if (email.isNotEmpty()) {
                    Auth.resetPasswordEmail(this@ResetPasswordActivity, email)
                } else {
                    Toast.makeText(this, "Email is required!", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}