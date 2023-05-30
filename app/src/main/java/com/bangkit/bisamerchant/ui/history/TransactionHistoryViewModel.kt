package com.bangkit.bisamerchant.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.core.domain.model.Transaction
import com.bangkit.bisamerchant.core.domain.usecase.TransactionUseCase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase
) : ViewModel() {

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private var listenerRegistration: ListenerRegistration? = null
    private val _totalAmountTransaction = MutableLiveData<Long>()

    val totalAmountTransaction: LiveData<Long> get() = _totalAmountTransaction

    fun observeTransactions() {
        listenerRegistration = transactionUseCase.observeTransactions { transactions ->
            _transactions.value = transactions
            _totalAmountTransaction.value =
                transactionUseCase.getTotalAmountTransactions(transactions)
        }
    }

    fun observeTransactionsWithFilter(
        queryDirection: Query.Direction?,
        startDate: Long?,
        endDate: Long?,
        trxType: String?,
    ) {
        viewModelScope.launch {
            listenerRegistration = transactionUseCase.observeTransactionsWithFilter(
                queryDirection = queryDirection,
                startDate = startDate,
                endDate = endDate,
                trxType = trxType,
            ) { transactions ->
                _transactions.value = transactions
                _totalAmountTransaction.value =
                    transactionUseCase.getTotalAmountTransactions(transactions)
            }
        }
    }

    fun stopObserving() {
        transactionUseCase.stopObserving()
    }

    override fun onCleared() {
        super.onCleared()
        transactionUseCase.stopObserving()
    }
}