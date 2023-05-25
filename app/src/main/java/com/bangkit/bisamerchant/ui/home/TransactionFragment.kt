package com.bangkit.bisamerchant.ui.home

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.bisamerchant.data.response.Transaction
import com.bangkit.bisamerchant.databinding.FragmentTransactionBinding
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.bangkit.bisamerchant.helper.Utils
import com.bangkit.bisamerchant.helper.ViewModelTransactionFactory
import com.bangkit.bisamerchant.ui.invoice.DetailTransactionActivity
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data")

class TransactionFragment : Fragment() {
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val transactionViewModel = initTransactionViewModel()
        updateUI(transactionViewModel)
    }

    private fun initTransactionViewModel(): TransactionViewModel {
        val pref = MerchantPreferences.getInstance(requireContext().dataStore)
        val factory = ViewModelTransactionFactory.getInstance(pref)
        val transactionViewModel: TransactionViewModel by viewModels { factory }
        return transactionViewModel
    }

    private fun updateUI(
        transactionViewModel: TransactionViewModel
    ) {
        transactionViewModel.observeTransactionsToday()
        binding.tvDate.text = Utils.getCurrentDate()

        lifecycleScope.launch {
            transactionViewModel.messageNotif.observe(viewLifecycleOwner) { message ->
                Utils.sendNotification(
                    context = requireContext(),
                    contentIntent = contentIntent(),
                    channelId = CHANNEL_ID,
                    channelName = CHANNEL_NAME,
                    notificationId = NOTIFICATION_ID,
                    resources = resources,
                    message = message,
                )
            }
            transactionViewModel.totalAmountTransactionToday.observe(viewLifecycleOwner) { amount ->
                binding.tvAmountDailyTransactions.text = Utils.currencyFormat(amount)
            }
            transactionViewModel.transactions.observe(viewLifecycleOwner) { transactions ->
                setTransactionsData(transactions)
                if (transactions.isNotEmpty()) {
                    binding.tvNoTransactions.visibility = View.GONE
                } else {
                    binding.tvNoTransactions.visibility = View.VISIBLE
                }
            }
            showRecyclerFollows()
        }
    }

    private fun showRecyclerFollows() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvTransactions.addItemDecoration(itemDecoration)
    }

    private fun setTransactionsData(transactions: List<Transaction>) {
        val listFollows = ArrayList<Transaction>()
        for (transaction in transactions) {
            listFollows.add(transaction)
        }
        val adapter = TransactionAdapter(listFollows)
        binding.rvTransactions.adapter = adapter
    }

    private fun contentIntent(): PendingIntent {
        val notificationIntent = Intent(context, DetailTransactionActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_MUTABLE
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "Transaksi_masuk"
        private const val CHANNEL_NAME = "Transaksi"
    }
}