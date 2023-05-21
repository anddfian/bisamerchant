package com.bangkit.bisamerchant.ui.history

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
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
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data")

class TransactionHistoryActivity : AppCompatActivity() {

    private var _binding: ActivityTransactionHistoryBinding? = null
    private val binding get() = _binding!!

    private var _filterBottomSheetBinding: FilterBottomSheetBinding? = null
    private val filterBottomSheetBinding get() = _filterBottomSheetBinding!!

    private var _bottomSheetDialog: BottomSheetDialog? = null
    private val bottomSheetDialog get() = _bottomSheetDialog!!

    private var startDate: Long? = null
    private var endDate: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTransactionHistoryBinding.inflate(layoutInflater)

        setupFilterBottomSheet()
        setContentView(binding.root)

        val transactionHistoryViewModel = initTransactionHistoryViewModel()
        updateUI(transactionHistoryViewModel)
        initClickListener(transactionHistoryViewModel)
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

    private fun setupFilterBottomSheet() {
        _filterBottomSheetBinding = FilterBottomSheetBinding.inflate(layoutInflater)
        _bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(filterBottomSheetBinding.root)
    }

    private fun initClickListener(transactionHistoryViewModel: TransactionHistoryViewModel) {
        var trxType: String? = null
        var queryDirection: Query.Direction = Query.Direction.DESCENDING

        binding.btnFilter.setOnClickListener {
            val radioFilter: RadioGroup = filterBottomSheetBinding.radioFilter
            radioFilter.setOnCheckedChangeListener { _, checkedId ->
                trxType = when (checkedId) {
                    R.id.rb_transaction_in -> {
                        "PAYMENT"
                    }

                    R.id.rb_transaction_out -> {
                        "MERCHANT_WITHDRAW"
                    }

                    else -> {
                        null
                    }
                }
            }

            val radioSort: RadioGroup = filterBottomSheetBinding.radioSort
            radioSort.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rb_newest -> {
                        queryDirection = Query.Direction.DESCENDING
                    }

                    R.id.rb_oldest -> {
                        queryDirection = Query.Direction.ASCENDING
                    }
                }
            }

            datePickerHandler { startDatePicked, endDatePicked ->
                startDate = startDatePicked
                endDate = endDatePicked
            }

            filterBottomSheetBinding.btnApply.setOnClickListener {
                applyFilter(
                    transactionHistoryViewModel, queryDirection, startDate, endDate, trxType
                )
                bottomSheetDialog.dismiss()
            }
        }
    }

    private fun datePickerHandler(callback: (Long?, Long?) -> Unit) {
        filterBottomSheetBinding.etDateRange.setOnClickListener {

            val datePickerBuilder = MaterialDatePicker.Builder.dateRangePicker().build()

            datePickerBuilder.addOnPositiveButtonClickListener { selection ->
                val timeZoneLocal = TimeZone.getDefault()
                val timeZoneUTCTimeOffset = timeZoneLocal.getOffset(selection.first)
                val startDate = selection.first - timeZoneUTCTimeOffset
                val endDate = selection.second - timeZoneUTCTimeOffset

                val selectedDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                selectedDateFormat.timeZone = TimeZone.getDefault()

                val formattedStartDate = selectedDateFormat.format(Date(startDate))
                val formattedEndDate = selectedDateFormat.format(Date(endDate))

                filterBottomSheetBinding.etDateRange.setText(
                    getString(
                        R.string.date_range_picked, formattedStartDate, formattedEndDate
                    )
                )

                callback(startDate, endDate)
            }

            datePickerBuilder.show(supportFragmentManager, DATE_PICKER_TAG)
        }

        bottomSheetDialog.show()
    }

    private fun applyFilter(
        transactionHistoryViewModel: TransactionHistoryViewModel,
        queryDirection: Query.Direction,
        startDate: Long?,
        endDate: Long?,
        trxType: String?
    ) {
        transactionHistoryViewModel.observeTransactionsWithFilter(
            queryDirection = queryDirection,
            startDate = startDate,
            endDate = endDate,
            trxType = trxType
        )
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
                binding.rvTransactions.visibility = View.VISIBLE
                binding.tvNoTransactions.visibility = View.GONE
            } else {
                binding.rvTransactions.visibility = View.GONE
                binding.tvNoTransactions.visibility = View.VISIBLE
            }

            if (startDate != null) {
                val formattedStartDate = Utils.simpleDateFormat(startDate, "dd MMM yyyy")
                val formattedEndDate = Utils.simpleDateFormat(endDate, "dd MMM yyyy")
                binding.tvDate.text = getString(
                    R.string.date_range_picked, formattedStartDate, formattedEndDate
                )
            }
        }
        showRecyclerHistories()
    }

    private fun showRecyclerHistories() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvTransactions.addItemDecoration(itemDecoration)
    }

    private fun setTransactionsData(transactions: List<Transaction>) {
        val listHistories = ArrayList<Transaction>()
        for (transaction in transactions) {
            listHistories.add(transaction)
        }
        val adapter = TransactionHistoryAdapter(listHistories)
        binding.rvTransactions.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        if (_binding == null) {
            _filterBottomSheetBinding = null
        }
    }

    companion object {
        private const val DATE_PICKER_TAG = "DATE_PICKER_TAG"
    }
}
