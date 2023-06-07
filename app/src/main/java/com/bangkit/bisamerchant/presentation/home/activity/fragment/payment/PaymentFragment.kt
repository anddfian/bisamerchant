package com.bangkit.bisamerchant.presentation.home.activity.fragment.payment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.FragmentPaymentBinding
import com.bangkit.bisamerchant.databinding.TransactionBottomSheetBinding
import com.bangkit.bisamerchant.domain.home.model.DetailTransaction
import com.bangkit.bisamerchant.presentation.home.viewmodel.HomeViewModel
import com.bangkit.bisamerchant.presentation.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.CaptureActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentFragment : Fragment() {
    private var scannedAmount: Long? = null
    private var _binding: FragmentPaymentBinding? = null

    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()

    private var _bottomSheetDialog: BottomSheetDialog? = null
    private val bottomSheetDialog get() = _bottomSheetDialog!!

    private var _transactionBottomSheetBinding: TransactionBottomSheetBinding? = null
    private val transactionBottomSheetBinding get() = _transactionBottomSheetBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        updateUI()
        setupFilterBottomSheet()
    }

    private fun updateUI() {
        generateStaticQRCode()
        homeViewModel.message.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    private fun setupFilterBottomSheet() {
        _transactionBottomSheetBinding =
            TransactionBottomSheetBinding.inflate(layoutInflater)
        _bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(transactionBottomSheetBinding.root)
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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

    private fun transactionConfirmation(detailTransaction: DetailTransaction) {
        bottomSheetDialog.show()
        transactionBottomSheetBinding.tvTransactionConfirmation.text = resources.getString(R.string.payment_confirmation)
        transactionBottomSheetBinding.tvAmountTransaction.text = resources.getString(R.string.rp, Utils.currencyFormat(detailTransaction.amount))
        transactionBottomSheetBinding.tvNumber.text = detailTransaction.payerId.toString()
        transactionBottomSheetBinding.subtotal.text = resources.getString(R.string.rp, Utils.currencyFormat(detailTransaction.amount))
        transactionBottomSheetBinding.tvTransactionType.text = resources.getString(R.string.payment)
        transactionBottomSheetBinding.btnContinueTransaction.setOnClickListener {
            homeViewModel.postTransaction(detailTransaction)
            bottomSheetDialog.dismiss()
        }
    }

    private val scanLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val intentResult = data?.getStringExtra(Intents.Scan.RESULT)
                val merchantId = homeViewModel.getMerchantId()
                Log.d("ScannedAmount", "$scannedAmount")

                if (!intentResult.isNullOrEmpty()) {
                    if (Utils.isValidQR(intentResult)) {
                        val listResult = intentResult.split("#")
                        scannedAmount?.let {
                            DetailTransaction(
                                amount = it,
                                merchantId = merchantId,
                                payerId = listResult[2],
                                timestamp = System.currentTimeMillis(),
                                trxType = "PAYMENT"
                            )
                        }?.let {
                            transactionConfirmation(
                                it
                            )
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.kode_tidak_valid),
                            Toast.LENGTH_SHORT
                        ).show()
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

    private fun generateDynamicQRCode(amount: String) {
        val ivDynamicQr = binding.ivDynamicQr
        val merchantId = homeViewModel.getMerchantId()
        val content = "DANA#MPM#$merchantId#$amount"

        val bitmap: Bitmap? = Utils.generateQRCode(content)
        ivDynamicQr.setImageBitmap(bitmap)
    }

    private fun generateStaticQRCode() {
        val ivStaticQr = binding.ivStaticQr
        val merchantId = homeViewModel.getMerchantId()
        val content = "DANA#MPM#$merchantId"

        val bitmap: Bitmap? = Utils.generateQRCode(content)
        ivStaticQr.setImageBitmap(bitmap)
    }

    private fun initClickListener() {
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
                    btnShareQrDynamic.visibility = View.GONE
                    btnScanQr.visibility = View.GONE
                    btnCreateQr.visibility = View.GONE
                }
            }
        }

        binding.btnCreateQr.setOnClickListener {
            binding.apply {
                if (binding.edPaymentAmount.text?.isNotEmpty() == true) {
                    generateDynamicQRCode(binding.edPaymentAmount.text.toString())
                    ivDynamicQr.visibility = View.VISIBLE
                    btnShareQrDynamic.visibility = View.VISIBLE
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
        binding.btnShareQr.setOnClickListener {
            binding.apply {
                val bitmap1 = (ivStaticQr.drawable as BitmapDrawable).bitmap
                val bitmap2: Bitmap =
                    BitmapFactory.decodeResource(resources, R.drawable.qr_share_background)
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
        binding.btnShareQrDynamic.setOnClickListener {
            binding.apply {
                val bitmap1 = (ivDynamicQr.drawable as BitmapDrawable).bitmap
                val bitmap2: Bitmap =
                    BitmapFactory.decodeResource(resources, R.drawable.qr_share_background)
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
        if (_binding == null) {
            _transactionBottomSheetBinding = null
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}