package com.bangkit.bisamerchant.ui.home.transaction

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.bisamerchant.core.data.model.Transaction
import com.bangkit.bisamerchant.core.helper.Utils
import com.bangkit.bisamerchant.databinding.FragmentTransactionBinding
import com.bangkit.bisamerchant.ui.home.TransactionViewModel
import com.bangkit.bisamerchant.ui.invoice.DetailTransactionActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionFragment : Fragment() {
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private val transactionViewModel: TransactionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI(transactionViewModel)
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