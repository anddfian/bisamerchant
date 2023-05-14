package com.bangkit.bisamerchant.ui.register

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityMerchantRegisterBinding
import com.bangkit.bisamerchant.services.Merchant

class MerchantRegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityMerchantRegisterBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.data != null) {
            selectedImageUri = result.data!!.data
            binding.ivRegistUpload.setImageURI(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMerchantRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_regist_upload -> {
                val resultIntent = Intent(Intent.ACTION_GET_CONTENT)
                resultIntent.type = "image/*"
                resultLauncher.launch(resultIntent)
            }
            R.id.btn_register_merchant -> {
                val name = binding.tilRegistMerchantName.editText?.text.toString()
                val location = binding.tilRegistMerchantLocation.editText?.text.toString()
                val type = binding.tilRegistMerchantType.editText?.text.toString()
                if (selectedImageUri == null) {
                    Toast.makeText(this@MerchantRegisterActivity, "Photo is required!", Toast.LENGTH_SHORT).show()
                } else if (name.isEmpty()) {
                    Toast.makeText(this@MerchantRegisterActivity, "Name is required!", Toast.LENGTH_SHORT).show()
                } else if (location.isEmpty()) {
                    Toast.makeText(this@MerchantRegisterActivity, "Location is required!", Toast.LENGTH_SHORT).show()
                } else {
                    Merchant.addMerchant(this@MerchantRegisterActivity, selectedImageUri!!, name, location, type)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.ivRegistUpload.setOnClickListener(this)
        binding.btnRegisterMerchant.setOnClickListener(this)
    }
}