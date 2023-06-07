package com.bangkit.bisamerchant.presentation.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.domain.profile.model.Merchant
import com.bangkit.bisamerchant.domain.profile.usecase.GetMerchantActive
import com.bangkit.bisamerchant.domain.profile.usecase.GetTotalTransactionsFromLastMonth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMerchantActive: GetMerchantActive,
    private val getTotalTransactionsFromLastMonth: GetTotalTransactionsFromLastMonth,
) : ViewModel() {

    private val _merchant = MutableLiveData<Merchant>()
    val merchant: LiveData<Merchant> get() = _merchant

    private val _totalAmountTransactionsFromLastMonth = MutableLiveData<Long>()
    val totalAmountTransactionsFromLastMonth: LiveData<Long> get() = _totalAmountTransactionsFromLastMonth

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var listenerRegistration: ListenerRegistration? = null

    fun getMerchantActive() {
        viewModelScope.launch {
            listenerRegistration = getMerchantActive.execute { merchant ->
                _merchant.value = merchant
            }
        }
    }

    fun getTotalTransactionsFromLastMonth() {
        viewModelScope.launch {
            getTotalTransactionsFromLastMonth.execute()
                .onStart {
                    _isLoading.value = true
                }
                .catch { e ->
                    _totalAmountTransactionsFromLastMonth.value = 0L
                    _isLoading.value = false
                }
                .collect { result ->
                    _isLoading.value = false
                    _totalAmountTransactionsFromLastMonth.value = result
                }
        }
    }
}