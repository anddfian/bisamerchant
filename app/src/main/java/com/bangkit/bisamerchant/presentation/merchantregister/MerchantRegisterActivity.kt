package com.bangkit.bisamerchant.presentation.merchantregister

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityMerchantRegisterBinding
import com.bangkit.bisamerchant.core.services.Merchant
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MerchantRegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityMerchantRegisterBinding? = null
    private val binding get() = _binding

    private var selectedImageUri: Uri? = null

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.data?.data != null) {
            var fileSize: Long = -1
            val cursor: Cursor? =
                contentResolver.query(result.data!!.data!!, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        fileSize = it.getLong(sizeIndex) / (1024 * 1024)
                    }
                }
            }
            if (fileSize > 5) {
                Snackbar.make(
                    binding?.root!!,
                    "Image size larger than 5MB",
                    Snackbar.LENGTH_SHORT
                ).setBackgroundTint(
                    ContextCompat.getColor(
                        this@MerchantRegisterActivity,
                        R.color.md_theme_light_error
                    )
                ).show()
            } else {
                selectedImageUri = result.data!!.data
                binding?.ivMerchantLogo?.setImageURI(selectedImageUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMerchantRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupClickListeners()
        updateUI()

        binding?.btnRegisterMerchant?.isEnabled = false

        val textfields = listOf(
            binding?.tilRegistMerchantName?.editText,
            binding?.tilRegistMerchantAddress?.editText,
            binding?.tilRegistMerchantType?.editText,
        )

        textfields.forEach { it ->
            it?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val allFilled = textfields.all { it?.text?.isNotEmpty() ?: false }
                    binding?.btnRegisterMerchant?.isEnabled = allFilled
                }
            })
        }
    }

    private fun updateUI() {
        val merchantItems = arrayOf(
            "Restoran",
            "Cafe",
            "Toko Baju",
            "Minimarket",
            "Apotek",
            "Salon",
            "Barbershop",
            "Warung Makan",
            "Kedai Kopi",
            "Butik",
            "Toko Elektronik",
            "Supermarket",
            "Bengkel Motor",
            "Bengkel Mobil",
            "Toko Perlengkapan Bayi",
            "Gudang",
            "Pasar Swalayan",
            "Toko Sepatu",
            "Pabrik",
            "Toko Perhiasan",
            "Toko Alat Tulis",
            "Kantor Desa",
            "Toko Furniture",
            "Galeri Seni",
            "Studio Fotografi",
            "Toko Roti",
            "Toko Mainan",
            "Kios",
            "Restoran Cepat Saji",
            "Toko Handphone",
            "Bakery",
            "Toko Buku",
            "Sekolah",
            "Universitas",
            "Hotel",
            "Motel",
            "Rumah Makan Padang",
            "Rumah Makan Jawa",
            "Toko Oleh-Oleh",
            "Pusat Perbelanjaan",
            "Toko Aksesoris",
            "Tempat Hiburan",
            "Toko Souvenir",
            "Rumah Makan Betawi",
            "Toko Jam Tangan",
            "Bengkel Elektronik",
            "Toko Komputer",
            "Toko Barang Antik",
            "Toko Kain",
            "Katering"
        )

        (binding?.edMerchantType as? MaterialAutoCompleteTextView)?.setSimpleItems(merchantItems)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_merchant_logo -> {
                val resultIntent = Intent(Intent.ACTION_GET_CONTENT)
                resultIntent.type = "image/*"
                resultLauncher.launch(resultIntent)
            }

            R.id.btn_register_merchant -> {
                val name = binding?.tilRegistMerchantName?.editText?.text.toString()
                val address = binding?.tilRegistMerchantAddress?.editText?.text.toString()
                val type = binding?.tilRegistMerchantType?.editText?.text.toString()
                when {
                    selectedImageUri == null -> {
                        Snackbar.make(
                            binding?.root!!,
                            "Photo is required!",
                            Snackbar.LENGTH_SHORT
                        ).setBackgroundTint(
                            ContextCompat.getColor(
                                this@MerchantRegisterActivity,
                                R.color.md_theme_light_error
                            )
                        ).show()
                    }

                    name.length > 50 -> {
                        binding?.tilRegistMerchantName?.error =
                            "Nama toko tidak boleh lebih dari 50 karakter!"
                    }

                    address.length > 100 -> {
                        binding?.tilRegistMerchantAddress?.error =
                            "Alamat toko tidak boleh lebih dari 100 karakter!"
                    }

                    type.isEmpty() -> {
                        binding?.tilRegistMerchantType?.error = "Tipe toko tidak boleh kosong!"
                    }

                    else -> {
                        Merchant.addMerchant(
                            this@MerchantRegisterActivity,
                            selectedImageUri!!,
                            name,
                            address,
                            type
                        )
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
        binding?.ivMerchantLogo?.setOnClickListener(this)
        binding?.btnRegisterMerchant?.setOnClickListener(this)
    }
}