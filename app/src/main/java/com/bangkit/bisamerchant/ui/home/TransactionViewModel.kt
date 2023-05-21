package com.bangkit.bisamerchant.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.data.TransactionRepository
import com.bangkit.bisamerchant.data.response.MessageNotif
import com.bangkit.bisamerchant.data.response.Payment
import com.bangkit.bisamerchant.data.response.Transaction
import com.bangkit.bisamerchant.helper.Utils
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _messageNotif = MutableLiveData<MessageNotif>()
    val messageNotif: LiveData<MessageNotif> get() = _messageNotif

    private var listenerRegistration: ListenerRegistration? = null

    private val _totalAmountTransactionToday = MutableLiveData<Long>()
    val totalAmountTransactionToday: LiveData<Long> get() = _totalAmountTransactionToday

    fun observeTransactionsToday() {
        viewModelScope.launch {
            listenerRegistration = repository.observeTransactionsToday { transactions ->
                _transactions.value = transactions
                _totalAmountTransactionToday.value =
                    repository.getTotalAmountTransactions(transactions)


                val totalTransaction = getTransactionCount()
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
                }
                saveTransactionCount(transactions.size)
            }
        }
    }

    fun stopObserving() {
        repository.stopObserving()
    }

    fun getTransactionCount() =
        repository.getTransactionCount()

    fun saveTransactionCount(count: Int) {
        repository.saveTransactionCount(count)
    }

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