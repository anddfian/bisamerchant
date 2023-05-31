package com.bangkit.bisamerchant.presentation.history.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.bisamerchant.domain.history.model.FilteredTransaction
import com.bangkit.bisamerchant.domain.history.model.Transaction
import com.bangkit.bisamerchant.domain.history.usecase.GetFilteredTransactions
import com.bangkit.bisamerchant.domain.history.usecase.GetTotalAmountTransactions
import com.bangkit.bisamerchant.domain.history.usecase.GetTransactions
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getFilteredTransactions: GetFilteredTransactions,
    private val getTotalAmountTransactions: GetTotalAmountTransactions,
    private val getTransactions: GetTransactions,
): ViewModel() {
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private var listenerRegistration: ListenerRegistration? = null
    private val _totalAmountTransaction = MutableLiveData<Long>()

    val totalAmountTransaction: LiveData<Long> get() = _totalAmountTransaction

    suspend fun getTransactions() {
        listenerRegistration = getTransactions.execute { transactions ->
            _transactions.value = transactions
            _totalAmountTransaction.value =
                getTotalAmountTransactions.execute(transactions)
        }
    }

    suspend fun getTransactionsWithFilter(
        filteredTransaction: FilteredTransaction
    ) {
        listenerRegistration = getFilteredTransactions.execute(
            filteredTransaction
        ) { transactions ->
            _transactions.value = transactions
            _totalAmountTransaction.value =
                getTotalAmountTransactions.execute(transactions)
        }
    }
}