package com.bangkit.bisamerchant.presentation.invoice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.domain.invoice.model.DetailTransaction
import com.bangkit.bisamerchant.domain.invoice.usecase.GetTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailTransactionViewModel @Inject constructor(
    private val getTransaction: GetTransaction
) : ViewModel() {
    private val _transaction = MutableLiveData<DetailTransaction>()
    val transaction: LiveData<DetailTransaction> get() = _transaction

    fun getTransactionById(id: String) {
        viewModelScope.launch {
            val data = getTransaction.execute(id)
            _transaction.value = data
        }
    }
}