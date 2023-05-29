package com.bangkit.bisamerchant.ui.setting

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityMerchantSettingBinding
import com.bangkit.bisamerchant.core.services.Merchant
import com.bangkit.bisamerchant.ui.home.MerchantViewModel
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MerchantSettingActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityMerchantSettingBinding? = null
    private val binding get() = _binding

    private val merchantViewModel: MerchantViewModel by viewModels()

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
                        this@MerchantSettingActivity,
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
        _binding = ActivityMerchantSettingBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initTopAppBar()
        setupClickListeners()

        updateUI(merchantViewModel)
        binding?.btnSaveMerchant?.isEnabled = false

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
                    binding?.btnSaveMerchant?.isEnabled = allFilled
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_merchant_logo -> {
                val resultIntent = Intent(Intent.ACTION_GET_CONTENT)
                resultIntent.type = "image/*"
                resultLauncher.launch(resultIntent)
            }

            R.id.btn_save_merchant -> {
                val name = binding?.tilRegistMerchantName?.editText?.text.toString()
                val address = binding?.tilRegistMerchantAddress?.editText?.text.toString()
                val type = binding?.tilRegistMerchantType?.editText?.text.toString()
                when {
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
                            this@MerchantSettingActivity,
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

    private fun initTopAppBar() {
        setSupportActionBar(binding?.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = "Setting Merchant"
        }
    }

    private fun setupClickListeners() {
        binding?.ivMerchantLogo?.setOnClickListener(this)
        binding?.btnSaveMerchant?.setOnClickListener(this)
    }

    private fun updateUI(
        merchantViewModel: MerchantViewModel,
    ) {
        merchantViewModel.observeMerchantActive()
        merchantViewModel.merchant.observe(this) { merchant ->
            binding?.apply {
                tilRegistMerchantName.editText?.setText(merchant.merchantName)
                tilRegistMerchantAddress.editText?.setText(merchant.merchantAddress)
                tilRegistMerchantType.editText?.setText(merchant.merchantType)
            }
            binding?.let {
                Glide.with(it.root)
                    .load(merchant.merchantLogo)
                    .placeholder(R.drawable.ic_loading_24)
                    .into(binding?.ivMerchantLogo!!)
            }
        }
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
}