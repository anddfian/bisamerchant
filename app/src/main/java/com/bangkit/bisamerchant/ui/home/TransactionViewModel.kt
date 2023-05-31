package com.bangkit.bisamerchant.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.core.domain.model.MessageNotif
import com.bangkit.bisamerchant.core.domain.model.Payment
import com.bangkit.bisamerchant.core.domain.model.Transaction
import com.bangkit.bisamerchant.core.domain.usecase.TransactionUseCase
import com.bangkit.bisamerchant.core.helper.Utils
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase
) : ViewModel() {
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _messageNotif = MutableLiveData<MessageNotif>()
    val messageNotif: LiveData<MessageNotif> get() = _messageNotif

    private var listenerRegistration: ListenerRegistration? = null

    private val _totalAmountTransactionToday = MutableLiveData<Long>()
    val totalAmountTransactionToday: LiveData<Long> get() = _totalAmountTransactionToday

    fun observeTransactionsToday() {
        viewModelScope.launch {
            val totalTransaction = getTransactionCount()
            val totalTransactionNew = MutableLiveData<Int>()
            listenerRegistration = transactionUseCase.observeTransactionsToday { transactions ->
                _transactions.value = transactions
                _totalAmountTransactionToday.value =
                    transactionUseCase.getTotalAmountTransactions(transactions)
                if (totalTransaction > 0 && transactions.size > totalTransaction) {
                    if (transactions[0].trxType == "PAYMENT") {
                        _messageNotif.value = MessageNotif(
                            "Pembayaran telah berhasil",
                            "Uang sejumlah Rp${Utils.currencyFormat(transactions[0].amount)} berhasil diterima",
                            "Pembayaran",
                        )
                    } else {
                        _messageNotif.value = MessageNotif(
                            "Penarikan telah berhasil",
                            "Uang sejumlah Rp${Utils.currencyFormat(transactions[0].amount)} berhasil ditarik",
                            "Tarik Dana",
                        )
                    }
                    totalTransactionNew.value = transactions.size
                }
            }
            totalTransactionNew.value?.let { saveTransactionCount(it) }
        }
    }

    fun stopObserving() {
        transactionUseCase.stopObserving()
    }

    suspend fun getTransactionCount() =
        transactionUseCase.getTransactionCount()

    suspend fun saveTransactionCount(count: Int) {
        transactionUseCase.saveTransactionCount(count)
    }

    override fun onCleared() {
        super.onCleared()
        transactionUseCase.stopObserving()
    }


    fun addTransaction(
        payment: Payment
    ) {
        viewModelScope.launch {
            transactionUseCase.addTransaction(payment)
                .onStart {
                    _isLoading.value = true
                }
                .catch { e ->
                    _message.value = "Terjadi kesalahan: ${e.message}"
                }
                .collect { result ->
                    _isLoading.value = false
                    _message.value = result
                }
        }
    }
}