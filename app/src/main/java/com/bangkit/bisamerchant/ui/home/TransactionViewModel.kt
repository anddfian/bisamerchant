package com.bangkit.bisamerchant.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.data.TransactionRepository
import com.bangkit.bisamerchant.data.response.Payment
import com.bangkit.bisamerchant.data.response.Transaction
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private var listenerRegistration: ListenerRegistration? = null

    private val _totalAmountTransactionToday = MutableLiveData<Long>()
    val totalAmountTransactionToday: LiveData<Long> get() = _totalAmountTransactionToday

    fun observeTransactionsToday() {
        viewModelScope.launch {
            listenerRegistration = repository.observeTransactionsToday { transactions ->
                _transactions.value = transactions
                _totalAmountTransactionToday.value = repository.getTotalAmountTransactions(transactions)
            }
        }
    }
    fun stopObserving() {
        repository.stopObserving()
    }

    fun getMerchantId() =
        repository.getMerchantId()

    override fun onCleared() {
        super.onCleared()
        repository.stopObserving()
    }


    fun addTransaction(
        payment: Payment
    ) {
        viewModelScope.launch {
            _message.value = repository.addTransaction(payment)
        }
    }
}