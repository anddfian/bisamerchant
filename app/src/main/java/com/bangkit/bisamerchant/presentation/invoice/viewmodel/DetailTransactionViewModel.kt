package com.bangkit.bisamerchant.presentation.invoice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.domain.invoice.model.DetailTransaction
import com.bangkit.bisamerchant.domain.invoice.usecase.GetTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailTransactionViewModel @Inject constructor(
    private val getTransaction: GetTransaction
) : ViewModel() {

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _transaction = MutableLiveData<DetailTransaction>()
    val transaction: LiveData<DetailTransaction> get() = _transaction

    fun getTransactionById(id: String) {
        viewModelScope.launch {
            getTransaction.execute(id)
                .onStart {
                    _isLoading.value = true
                }.catch { e ->
                    _message.value = e.message.toString()
                    _isLoading.value = false
                }.collect { result ->
                    _isLoading.value = false
                    _transaction.value = result
                }
        }
    }
}