package com.bangkit.bisamerchant.ui.invoice

import android.content.Context
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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.databinding.ActivityDetailTransactionBinding
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.bangkit.bisamerchant.helper.Utils
import com.bangkit.bisamerchant.helper.ViewModelTransactionFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data")

class DetailTransactionActivity : AppCompatActivity() {
    private var _binding: ActivityDetailTransactionBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailTransactionBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initTopAppBar()
        val detailTransactionViewModel = initDetailTransactionViewModel()
        updateUI(detailTransactionViewModel)
    }

    private fun updateUI(detailTransactionViewModel: DetailTransactionViewModel) {
        val id = intent.getStringExtra(EXTRA_ID)

        if (id != null) {
            detailTransactionViewModel.getTransactionById(id)
        }

        detailTransactionViewModel.transactionn.observe(this) { transaction ->
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

    private fun initDetailTransactionViewModel(): DetailTransactionViewModel {
        val pref = MerchantPreferences.getInstance(dataStore)
        val factory = ViewModelTransactionFactory.getInstance(pref)
        val transactionViewModel: DetailTransactionViewModel by viewModels { factory }
        return transactionViewModel
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
                val bitmap1 = Utils.layoutToBitmap(invoice!!)
                val bitmap2 : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.invoice_share_background)
                val mergedBitmap = Utils.invoiceSharedBitmap(bitmap2, bitmap1)
                val uri = Utils.bitmapToTempFile(this, mergedBitmap)
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