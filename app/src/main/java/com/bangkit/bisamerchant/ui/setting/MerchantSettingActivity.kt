package com.bangkit.bisamerchant.ui.setting

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityMerchantSettingBinding
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.bangkit.bisamerchant.helper.ViewModelMerchantFactory
import com.bangkit.bisamerchant.services.Merchant
import com.bangkit.bisamerchant.ui.home.MerchantViewModel
import com.bumptech.glide.Glide
import com.google.android.material.textfield.MaterialAutoCompleteTextView

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data")

class MerchantSettingActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityMerchantSettingBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.data?.data != null) {
            var fileSize: Long = -1
            val cursor: Cursor? = contentResolver.query(result.data!!.data!!, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        fileSize = it.getLong(sizeIndex) / (1024 * 1024)
                    }
                }
            }
            if (fileSize > 5) {
                Toast.makeText(this@MerchantSettingActivity, "Image size larger than 5MB", Toast.LENGTH_SHORT).show()
            } else {
                selectedImageUri = result.data!!.data
                binding.ivMerchantLogo.setImageURI(selectedImageUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMerchantSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTopAppBar()
        setupClickListeners()
        val merchantViewModel = initMerchantViewModel()
        updateUI(merchantViewModel)
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
                val name = binding.tilRegistMerchantName.editText?.text.toString()
                val address = binding.tilRegistMerchantAddress.editText?.text.toString()
                val type = binding.tilRegistMerchantType.editText?.text.toString()
                if (name.isEmpty()) {
                    Toast.makeText(this@MerchantSettingActivity, "Name is required!", Toast.LENGTH_SHORT).show()
                } else if (address.isEmpty()) {
                    Toast.makeText(this@MerchantSettingActivity, "Address is required!", Toast.LENGTH_SHORT).show()
                } else if (type.isEmpty()) {
                    Toast.makeText(this@MerchantSettingActivity, "Type is required!", Toast.LENGTH_SHORT).show()
                } else {
                    Merchant.updateMerchant(this, this@MerchantSettingActivity, selectedImageUri, name, address, type)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initTopAppBar() {
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = "Setting Merchant"
        }
    }

    private fun initMerchantViewModel(): MerchantViewModel {
        val pref = MerchantPreferences.getInstance(dataStore)
        val factory = ViewModelMerchantFactory.getInstance(pref)
        val merchantViewModel: MerchantViewModel by viewModels { factory }
        return merchantViewModel
    }

    private fun setupClickListeners() {
        binding.ivMerchantLogo.setOnClickListener(this)
        binding.btnSaveMerchant.setOnClickListener(this)
    }

    private fun updateUI(
        merchantViewModel: MerchantViewModel,
    ) {
        merchantViewModel.observeMerchantActive()
        merchantViewModel.merchant.observe(this) { merchant ->
            binding.apply {
                tilRegistMerchantName.editText?.setText(merchant.merchantName)
                tilRegistMerchantAddress.editText?.setText(merchant.merchantAddress)
                tilRegistMerchantType.editText?.setText(merchant.merchantType)
            }
            Glide.with(binding.root)
                .load(merchant.merchantLogo)
                .placeholder(R.drawable.ic_loading_24)
                .into(binding.ivMerchantLogo)
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
        (binding.edMerchantType as? MaterialAutoCompleteTextView)?.setSimpleItems(merchantItems)
    }
}