package com.bangkit.bisamerchant.ui.history

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.data.response.Transaction
import com.bangkit.bisamerchant.databinding.ActivityTransactionHistoryBinding
import com.bangkit.bisamerchant.databinding.FilterBottomSheetBinding
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.bangkit.bisamerchant.helper.Utils
import com.bangkit.bisamerchant.helper.ViewModelTransactionFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("merchant")

class TransactionHistoryActivity : AppCompatActivity() {

    private var _binding: ActivityTransactionHistoryBinding? = null
    private val binding get() = _binding!!

    private var _filterBottomSheetBinding: FilterBottomSheetBinding? = null
    private val filterBottomSheetBinding get() = _filterBottomSheetBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTransactionHistoryBinding.inflate(layoutInflater)
        _filterBottomSheetBinding = FilterBottomSheetBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val transactionHistory = initTransactionHistoryViewModel()
        updateUI(transactionHistory)
        initClickListener()
        initTopAppBar()
    }

    private fun initTopAppBar() {
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = getString(R.string.history)
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

    private fun initClickListener() {
        binding.btnFilter.setOnClickListener {
            datePickerHandler()
        }
    }

    private fun datePickerHandler() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(filterBottomSheetBinding.root)

        filterBottomSheetBinding.etDateRange.setOnClickListener {
            val datePickerBuilder = MaterialDatePicker.Builder.dateRangePicker()
            val materialDatePicker = datePickerBuilder.build()

            materialDatePicker.addOnPositiveButtonClickListener { selection ->
                val startDate = selection.first
                val endDate = selection.second

                val selectedDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val formattedStartDate = selectedDateFormat.format(Date(startDate ?: 0L))
                val formattedEndDate = selectedDateFormat.format(Date(endDate ?: 0L))

                filterBottomSheetBinding.etDateRange.setText("$formattedStartDate - $formattedEndDate")
            }

            materialDatePicker.show(supportFragmentManager, DATE_PICKER_TAG)
        }

        bottomSheetDialog.show()
    }

    private fun initTransactionHistoryViewModel(): TransactionHistoryViewModel {
        val pref = MerchantPreferences.getInstance(dataStore)
        val factory = ViewModelTransactionFactory.getInstance(pref)
        val transactionHistory: TransactionHistoryViewModel by viewModels { factory }
        return transactionHistory
    }

    private fun updateUI(
        transactionHistory: TransactionHistoryViewModel
    ) {
        transactionHistory.observeTransactions()
        transactionHistory.totalAmountTransaction.observe(this) { amount ->
            binding.tvAmount.text = Utils.currencyFormat(amount)
        }
        transactionHistory.transactions.observe(this) { transactions ->
            if (transactions.isNotEmpty()) {
                setTransactionsData(transactions)
                binding.tvNoTransactions.visibility = View.GONE
            } else {
                binding.tvNoTransactions.visibility = View.VISIBLE
            }
        }
        showRecyclerFollows()
    }

    private fun showRecyclerFollows() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvTransactions.addItemDecoration(itemDecoration)
    }

    private fun setTransactionsData(transactions: List<Transaction>) {
        val listFollows = ArrayList<Transaction>()
        for (transaction in transactions) {
            listFollows.add(transaction)
        }
        val adapter = TransactionHistoryAdapter(listFollows)
        binding.rvTransactions.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _filterBottomSheetBinding = null
    }

    companion object {
        private const val DATE_PICKER_TAG = "DATE_PICKER_TAG"
    }
}
