package com.bangkit.bisamerchant.ui.invoice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.core.data.model.DetailTransaction
import com.bangkit.bisamerchant.core.domain.usecase.TransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailTransactionViewModel @Inject constructor(private val transactionUseCase: TransactionUseCase) : ViewModel() {
    private val _transaction = MutableLiveData<DetailTransaction>()
    val transactionn: LiveData<DetailTransaction> get() = _transaction

    fun getTransactionById(id: String) {
        viewModelScope.launch {
            val documentSnapshot = transactionUseCase.getTransactionById(id)
            val data = transactionUseCase.processTransactionDocumentSnapshot(documentSnapshot)
            _transaction.value = data
        }
    }
}