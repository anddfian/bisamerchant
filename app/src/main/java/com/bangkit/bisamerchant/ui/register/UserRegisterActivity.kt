package com.bangkit.bisamerchant.ui.register

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityUserRegisterBinding
import com.bangkit.bisamerchant.services.Auth
import com.bangkit.bisamerchant.ui.termpolicy.PrivacyPolicyActivity
import com.bangkit.bisamerchant.ui.termpolicy.TermConditionActivity

class UserRegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityUserRegisterBinding? = null
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupClickListeners()

        binding?.btnRegister?.isEnabled = false

        val textfields = listOf(
            binding?.tilRegistName?.editText,
            binding?.tilRegistEmail?.editText,
            binding?.tilRegistPassword?.editText,
            binding?.tilRegistPin?.editText
        )

        textfields.forEach{ it ->
            it?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val allFilled = textfields.all { it?.text?.isNotEmpty() ?: false }
                    binding?.btnRegister?.isEnabled = allFilled
                }
            })
        }
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
                val name = binding?.tilRegistName?.editText?.text.toString()
                val email = binding?.tilRegistEmail?.editText?.text.toString()
                val password = binding?.tilRegistPassword?.editText?.text.toString()
                val pin = binding?.tilRegistPin?.editText?.text.toString()
                when {
                    name.length > 48 -> {
                        binding?.tilRegistName?.error = "Nama melebihi batas karakter!"
                    }
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        binding?.tilRegistEmail?.error = "Format email salah!"
                    }
                    password.length < 6 -> {
                        binding?.tilRegistPassword?.error = "Password kurang dari 6 digit!"
                    }
                    pin.length < 6 -> {
                        binding?.tilRegistPin?.error = "PIN kurang dari 6 digit!"
                    }
                    else -> {
                        Auth.register(this@UserRegisterActivity, name, email, password, pin)
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
        binding?.tvTerm?.setOnClickListener(this)
        binding?.tvPolicy?.setOnClickListener(this)
        binding?.btnLogin?.setOnClickListener(this)
        binding?.btnRegister?.setOnClickListener(this)
    }
}