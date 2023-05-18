package com.bangkit.bisamerchant.ui.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import com.bangkit.bisamerchant.databinding.FragmentPaymentBinding
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.bangkit.bisamerchant.helper.Utils
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("merchant")

class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = MerchantPreferences.getInstance(requireContext().dataStore)
        generateStaticQRCode(pref)
        initClickListener(pref)
    }


    private fun startQRCodeScanner() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            initiateScan()
        }
    }

    private val scanLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val intentResult = data?.getStringExtra(Intents.Scan.RESULT)
                if (!intentResult.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(), "Scanned Data: $intentResult", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(), "Something went wrong", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private fun initiateScan() {
        val intent = Intent(requireActivity(), CaptureActivity::class.java)
        intent.apply {
            action = Intents.Scan.ACTION
            putExtra(Intents.Scan.MODE, Intents.Scan.QR_CODE_MODE)
            putExtra(Intents.Scan.PROMPT_MESSAGE, "Scan a QR Code")
            putExtra(Intents.Scan.ORIENTATION_LOCKED, true)
        }

        scanLauncher.launch(intent)
    }

    private fun generateDynamicQRCode(amount: String, pref: MerchantPreferences) {
        val ivDynamicQr = binding.ivDynamicQr
        val merchantId = runBlocking { pref.getMerchantId().first() }
        val content = "DANA#MPM#$merchantId#$amount"

        val bitmap: Bitmap? = Utils.generateQRCode(content)
        ivDynamicQr.setImageBitmap(bitmap)
    }

    private fun generateStaticQRCode(pref: MerchantPreferences) {
        val ivStaticQr = binding.ivStaticQr
        val merchantId = runBlocking { pref.getMerchantId().first() }
        val content = "DANA#MPM#$merchantId"

        val bitmap: Bitmap? = Utils.generateQRCode(content)
        ivStaticQr.setImageBitmap(bitmap)
    }

    private fun initClickListener(pref: MerchantPreferences) {
        binding.switchQr.setOnClickListener {
            binding.apply {
                if (switchQr.isChecked) {
                    ivStaticQr.visibility = View.GONE
                    btnScanQr.visibility = View.VISIBLE
                    btnShareQr.visibility = View.GONE
                    btnCreateQr.visibility = View.VISIBLE
                    if (binding.edPaymentAmount.text?.isNotEmpty() == true) {
                        ivDynamicQr.visibility = View.VISIBLE
                    } else if (binding.edPaymentAmount.text?.isEmpty() == true) {
                        ivDynamicQr.visibility = View.GONE
                    }
                    edPaymentAmountLayout.visibility = View.VISIBLE
                } else {
                    ivStaticQr.visibility = View.VISIBLE
                    btnShareQr.visibility = View.VISIBLE
                    ivDynamicQr.visibility = View.GONE
                    edPaymentAmountLayout.visibility = View.GONE
                    edPaymentAmountLayout.visibility = View.GONE
                    btnScanQr.visibility = View.GONE
                    btnCreateQr.visibility = View.GONE
                }
            }
        }

        binding.btnCreateQr.setOnClickListener {
            binding.apply {
                if (binding.edPaymentAmount.text?.isNotEmpty() == true) {
                    generateDynamicQRCode(binding.edPaymentAmount.text.toString(), pref)
                    ivDynamicQr.visibility = View.VISIBLE
                } else {
                    Toast.makeText(requireContext(), "Amount cannot be empty", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
        binding.btnScanQr.setOnClickListener {
            binding.apply {
                if (binding.edPaymentAmount.text?.isNotEmpty() == true) {
                    startQRCodeScanner()
                } else {
                    Toast.makeText(requireContext(), "Amount cannot be empty", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        _binding?.paymentContainer?.minimumHeight = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}