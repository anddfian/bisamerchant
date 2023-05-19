package com.bangkit.bisamerchant.ui.register

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityMerchantRegisterBinding
import com.bangkit.bisamerchant.services.Merchant
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class MerchantRegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityMerchantRegisterBinding? = null
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
                Toast.makeText(this@MerchantRegisterActivity, "Image size larger than 5MB", Toast.LENGTH_SHORT).show()
            } else {
                selectedImageUri = result.data!!.data
                binding.ivMerchantLogo.setImageURI(selectedImageUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMerchantRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        updateUI()
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

        (binding.edMerchantType as? MaterialAutoCompleteTextView)?.setSimpleItems(merchantItems)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_merchant_logo -> {
                val resultIntent = Intent(Intent.ACTION_GET_CONTENT)
                resultIntent.type = "image/*"
                resultLauncher.launch(resultIntent)
            }
            R.id.btn_register_merchant -> {
                val name = binding.tilRegistMerchantName.editText?.text.toString()
                val address = binding.tilRegistMerchantAddress.editText?.text.toString()
                val type = binding.tilRegistMerchantType.editText?.text.toString()
                if (selectedImageUri == null) {
                    Toast.makeText(this@MerchantRegisterActivity, "Photo is required!", Toast.LENGTH_SHORT).show()
                } else if (name.isEmpty()) {
                    Toast.makeText(this@MerchantRegisterActivity, "Name is required!", Toast.LENGTH_SHORT).show()
                } else if (address.isEmpty()) {
                    Toast.makeText(this@MerchantRegisterActivity, "Address is required!", Toast.LENGTH_SHORT).show()
                } else if (type.isEmpty()) {
                    Toast.makeText(this@MerchantRegisterActivity, "Type is required!", Toast.LENGTH_SHORT).show()
                } else {
                    Merchant.addMerchant(this@MerchantRegisterActivity, selectedImageUri!!, name, address, type)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.ivMerchantLogo.setOnClickListener(this)
        binding.btnRegisterMerchant.setOnClickListener(this)
    }
}