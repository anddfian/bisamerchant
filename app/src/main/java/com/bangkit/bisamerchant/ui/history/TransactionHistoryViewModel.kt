package com.bangkit.bisamerchant.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.bisamerchant.core.data.TransactionRepository
import com.bangkit.bisamerchant.core.data.model.Transaction
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class TransactionHistoryViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private var listenerRegistration: ListenerRegistration? = null
    private val _totalAmountTransaction = MutableLiveData<Long>()

    val totalAmountTransaction: LiveData<Long> get() = _totalAmountTransaction

    fun observeTransactions() {
        listenerRegistration = repository.observeTransactions { transactions ->
            _transactions.value = transactions
            _totalAmountTransaction.value = repository.getTotalAmountTransactions(transactions)
        }
    }

    fun observeTransactionsWithFilter(
        startDate: Long?,
        endDate: Long?,
        queryDirection: Query.Direction,
        trxType: String?,
    ) {
        listenerRegistration = when {
            trxType != null && startDate != null && endDate != null -> {
                repository.observeTransactionsWithFilter(
                    queryDirection = queryDirection,
                    startDate = startDate,
                    endDate = endDate,
                    trxType = trxType
                ) { transactions ->
                    _transactions.value = transactions
                    _totalAmountTransaction.value =
                        repository.getTotalAmountTransactions(transactions)
                }
            }

            trxType == null && startDate != null && endDate != null -> {
                repository.observeTransactionsWithFilter(
                    queryDirection = queryDirection,
                    startDate = startDate,
                    endDate = endDate
                ) { transactions ->
                    _transactions.value = transactions
                    _totalAmountTransaction.value =
                        repository.getTotalAmountTransactions(transactions)
                }
            }

            trxType != null && startDate == null && endDate == null -> {
                repository.observeTransactionsWithFilter(
                    queryDirection = queryDirection,
                    trxType = trxType
                ) { transactions ->
                    _transactions.value = transactions
                    _totalAmountTransaction.value =
                        repository.getTotalAmountTransactions(transactions)
                }
            }

            else -> {
                repository.observeTransactionsWithFilter(
                    queryDirection = queryDirection
                ) { transactions ->
                    _transactions.value = transactions
                    _totalAmountTransaction.value =
                        repository.getTotalAmountTransactions(transactions)
                }
            }
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