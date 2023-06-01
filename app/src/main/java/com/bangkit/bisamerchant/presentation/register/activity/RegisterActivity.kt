package com.bangkit.bisamerchant.presentation.register.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityUserRegisterBinding
import com.bangkit.bisamerchant.presentation.merchantregister.MerchantRegisterActivity
import com.bangkit.bisamerchant.presentation.register.viewmodel.RegisterViewModel
import com.bangkit.bisamerchant.presentation.termpolicy.PrivacyPolicyActivity
import com.bangkit.bisamerchant.presentation.termpolicy.TermConditionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityUserRegisterBinding? = null
    private val binding get() = _binding

    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupClickListeners()
        updateUI()

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

    private fun updateUI() {
        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        registerViewModel.message.observe(this) {
            Toast.makeText(this@RegisterActivity, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.tv_term -> {
                val intent = Intent(this@RegisterActivity, TermConditionActivity::class.java)
                startActivity(intent)
            }
            R.id.tv_policy -> {
                val intent = Intent(this@RegisterActivity, PrivacyPolicyActivity::class.java)
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
                    name.length > 36 -> {
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
                        registerViewModel.registerOwner(name, email, password, pin)
                        registerViewModel.isRegisterSuccess.observe(this) {isSuccess ->
                            if (isSuccess) {
                                val intent = Intent(this@RegisterActivity, MerchantRegisterActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
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