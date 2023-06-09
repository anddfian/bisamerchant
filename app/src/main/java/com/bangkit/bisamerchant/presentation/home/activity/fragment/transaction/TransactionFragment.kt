package com.bangkit.bisamerchant.presentation.home.activity.fragment.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.bisamerchant.databinding.FragmentTransactionBinding
import com.bangkit.bisamerchant.domain.home.model.Transaction
import com.bangkit.bisamerchant.presentation.home.viewmodel.HomeViewModel
import com.bangkit.bisamerchant.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionFragment : Fragment() {
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI(homeViewModel)
    }

    private fun updateUI(
        homeViewModel: HomeViewModel
    ) {
        homeViewModel.getTransactionsToday()
        binding.tvDate.text = Utils.getCurrentDate()

        lifecycleScope.launch {
            homeViewModel.totalAmountTransactionToday.observe(viewLifecycleOwner) { amount ->
                binding.tvAmountDailyTransactions.text = Utils.currencyFormat(amount)
            }
            homeViewModel.transactions.observe(viewLifecycleOwner) { transactions ->
                setTransactionsData(transactions)
                if (transactions.isNotEmpty()) {
                    binding.tvNoTransactions.visibility = View.GONE
                } else {
                    binding.tvNoTransactions.visibility = View.VISIBLE
                }
            }
            showRecyclerFollows()
        }
        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        homeViewModel.message.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}