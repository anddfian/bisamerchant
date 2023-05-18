package com.bangkit.bisamerchant.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.bisamerchant.data.TransactionRepository
import com.bangkit.bisamerchant.data.response.Transaction
import com.google.firebase.firestore.ListenerRegistration

class TransactionHistoryViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private var listenerRegistration: ListenerRegistration? = null
    private val _totalAmountTransaction = MutableLiveData<Long>()

    val totalAmountTransaction: LiveData<Long> get() = _totalAmountTransaction

    fun observeTransactions() {
        listenerRegistration = repository.observeTransactions { transactions ->
            _transactions.value = transactions
            _totalAmountTransaction.value = repository.getTotalAmountTransactions(transactions)
        }
    }

    fun stopObserving() {
        repository.stopObserving()
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopObserving()
    }
}