package com.bangkit.bisamerchant.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.data.TransactionRepository
import com.bangkit.bisamerchant.data.response.Transaction
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _totalAmountTransactionToday = MutableLiveData<Long>()
    val totalAmountTransactionToday: LiveData<Long> get() = _totalAmountTransactionToday

    fun getTransactionToday() {
        viewModelScope.launch {
            val querySnapshot = repository.getTransactionsToday()
            val data = repository.processTransactionQuerySnapshot(querySnapshot)
            val totalAmount = repository.getTotalAmountTransactions(data)

            _transactions.value = data
            _totalAmountTransactionToday.value = totalAmount
        }
    }
}