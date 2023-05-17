package com.bangkit.bisamerchant.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.data.MerchantRepository
import com.bangkit.bisamerchant.data.response.Merchant
import kotlinx.coroutines.launch

class MerchantViewModel(private val repository: MerchantRepository) : ViewModel() {
    private val _merchant = MutableLiveData<Merchant>()
    val merchant: LiveData<Merchant> get() = _merchant

    fun getMerchantActive() {
        viewModelScope.launch {
            val querySnapshot = repository.getMerchantActive()
            val data = repository.processMerchantQuerySnapshot(querySnapshot)
            _merchant.value = data
        }
    }
}