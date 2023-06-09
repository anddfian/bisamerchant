package com.bangkit.bisamerchant.presentation.invoice.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityDetailTransactionBinding
import com.bangkit.bisamerchant.presentation.utils.Utils
import com.bangkit.bisamerchant.presentation.invoice.viewmodel.DetailTransactionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailTransactionActivity : AppCompatActivity() {
    private var _binding: ActivityDetailTransactionBinding? = null
    private val binding get() = _binding

    private val detailTransactionViewModel: DetailTransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailTransactionBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initTopAppBar()
        updateUI(detailTransactionViewModel)
    }

    private fun updateUI(detailTransactionViewModel: DetailTransactionViewModel) {
        val id = intent.getStringExtra(EXTRA_ID)

        if (id != null) {
            detailTransactionViewModel.getTransactionById(id)
        }

        detailTransactionViewModel.transaction.observe(this) { transaction ->
            if (transaction.trxType == "PAYMENT") {
                binding?.apply {
                    invoiceContainer.visibility = View.VISIBLE
                    payerIdContainer.visibility = View.VISIBLE
                    trxType.text = getString(R.string.payment)
                    merchantId.text = transaction.merchantId
                    payerId.text = transaction.payerId
                    transactionId.text = transaction.id
                    tvInvoiceDate.text = transaction.timestamp?.let {
                        Utils.simpleDateFormat(it, "EEE, d MMM yyyy – HH:mm")
                    }
                    totalAmount.text =
                        this@DetailTransactionActivity.getString(
                            R.string.rp,
                            Utils.currencyFormat(transaction.amount)
                        )
                }
            } else {
                binding?.apply {
                    invoiceContainer.visibility = View.VISIBLE
                    bankNameContainer.visibility = View.VISIBLE
                    accountNoContainer.visibility = View.VISIBLE
                    trxType.text = getString(R.string.withdraw)
                    merchantId.text = transaction.merchantId
                    bankName.text = transaction.bankInst
                    accountNo.text = transaction.bankAccountNo.toString()
                    transactionId.text = transaction.id
                    tvInvoiceDate.text = transaction.timestamp?.let {
                        Utils.simpleDateFormat(it, "EEE, d MMM yyyy – HH:mm")
                    }
                    totalAmount.text =
                        this@DetailTransactionActivity.getString(
                            R.string.rp,
                            Utils.currencyFormat(transaction.amount)
                        )
                }
            }
        }
    }

    private fun initTopAppBar() {
        setSupportActionBar(binding?.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = "Detail Transaksi"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.invoice_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            R.id.action_share -> {
                val invoice = binding?.invoiceContainer
                val bitmap1 = invoice?.let { Utils.layoutToBitmap(it) }
                val bitmap2 : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.invoice_share_background)
                val mergedBitmap = bitmap1?.let { Utils.invoiceSharedBitmap(bitmap2, it) }
                val uri = mergedBitmap?.let { Utils.bitmapToTempFile(this, it) }
                if (uri != null) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/png"
                        putExtra(Intent.EXTRA_STREAM, uri)
                    }
                    startActivity(Intent.createChooser(shareIntent, "Bagikan Gambar"))
                } else {
                    Toast.makeText(this, "Gagal membagikan gambar", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    companion object {
        const val EXTRA_ID = "extra_id"
    }
}