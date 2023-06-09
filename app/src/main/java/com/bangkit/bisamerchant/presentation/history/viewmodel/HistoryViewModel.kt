package com.bangkit.bisamerchant.presentation.history.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.bisamerchant.domain.history.model.FilteredTransaction
import com.bangkit.bisamerchant.domain.history.model.Transaction
import com.bangkit.bisamerchant.domain.history.usecase.GetFilteredTransactions
import com.bangkit.bisamerchant.domain.history.usecase.GetTotalAmountTransactions
import com.bangkit.bisamerchant.domain.history.usecase.GetTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getFilteredTransactions: GetFilteredTransactions,
    private val getTotalAmountTransactions: GetTotalAmountTransactions,
    private val getTransactions: GetTransactions,
) : ViewModel() {
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _totalAmountTransaction = MutableLiveData<Long>()
    val totalAmountTransaction: LiveData<Long> get() = _totalAmountTransaction

    suspend fun getTransactions() {
        getTransactions.execute()
            .onStart {
                _isLoading.value = true
            }
            .catch { e ->
                _message.value = e.message.toString()
                _isLoading.value = false
            }
            .collect { result ->
                _isLoading.value = false
                _transactions.value = result
                _totalAmountTransaction.value =
                    getTotalAmountTransactions.execute(result)
            }
    }

    suspend fun getTransactionsWithFilter(
        filteredTransaction: FilteredTransaction
    ) {
        getFilteredTransactions.execute(filteredTransaction)
            .onStart {
                _isLoading.value = true
            }
            .catch { e ->
                _message.value = e.message.toString()
                _isLoading.value = false
            }
            .collect { result ->
                _isLoading.value = false
                _transactions.value = result
                _totalAmountTransaction.value = getTotalAmountTransactions.execute(result)
            }
    }
}