package com.bangkit.bisamerchant.ui.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
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
import androidx.fragment.app.viewModels
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.data.response.Payment
import com.bangkit.bisamerchant.databinding.FragmentPaymentBinding
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.bangkit.bisamerchant.helper.Utils
import com.bangkit.bisamerchant.helper.ViewModelTransactionFactory
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data")

class PaymentFragment : Fragment() {
    private var scannedAmount: Long? = null
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
        val transactionViewModel = initTransactionViewModel(pref)
        initClickListener(pref)
        updateUI(pref, transactionViewModel)
    }

    private fun updateUI(pref: MerchantPreferences, transactionViewModel: TransactionViewModel) {
        generateStaticQRCode(pref)
        transactionViewModel.message.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    private fun initTransactionViewModel(pref: MerchantPreferences): TransactionViewModel {
        val factory = ViewModelTransactionFactory.getInstance(pref)
        val transactionViewModel: TransactionViewModel by viewModels { factory }
        return transactionViewModel
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
                val pref = MerchantPreferences.getInstance(requireContext().dataStore)
                val merchantId = runBlocking { pref.getMerchantId().first() }

                if (!intentResult.isNullOrEmpty()) {
                    val transactionViewModel = initTransactionViewModel(pref)
                    val listResult = intentResult.split("#")
                    scannedAmount?.let {
                        Payment(
                            amount = it,
                            merchantId = merchantId,
                            payerId = listResult[2],
                            timestamp = System.currentTimeMillis(),
                            trxType = "PAYMENT"
                        )
                    }?.let {
                        transactionViewModel.addTransaction(
                            it
                        )
                    }
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
                    scannedAmount = binding.edPaymentAmount.text.toString().toLong()
                    startQRCodeScanner()
                } else {
                    Toast.makeText(requireContext(), "Amount cannot be empty", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
        binding.btnShareQr.setOnClickListener{
            binding.apply {
                val bitmap1 = (ivStaticQr.drawable as BitmapDrawable).bitmap
                val bitmap2 : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.qr_share_background)
                val mergedBitmap = Utils.QRSharedBitmap(bitmap2, bitmap1)
                val uri = Utils.bitmapToTempFile(requireContext(), mergedBitmap)
                if (uri != null) {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/*"
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(Intent.createChooser(intent, "Share QR Code"))
                } else {
                    Toast.makeText(requireContext(), "Failed to share QR Code", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scannedAmount = null
        _binding = null
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}