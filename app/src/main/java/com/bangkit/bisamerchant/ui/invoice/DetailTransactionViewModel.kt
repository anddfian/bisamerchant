package com.bangkit.bisamerchant.ui.invoice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.data.TransactionRepository
import com.bangkit.bisamerchant.data.response.DetailTransaction
import kotlinx.coroutines.launch

class DetailTransactionViewModel(private val repository: TransactionRepository) : ViewModel() {
    private val _transaction = MutableLiveData<DetailTransaction>()
    val transactionn: LiveData<DetailTransaction> get() = _transaction

    fun getTransactionById(id: String) {
        viewModelScope.launch {
            val documentSnapshot = repository.getTransactionById(id)
            val data = repository.processTransactionDocumentSnapshot(documentSnapshot)
            _transaction.value = data
        }
    }
}