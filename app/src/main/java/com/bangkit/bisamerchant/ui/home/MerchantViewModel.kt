package com.bangkit.bisamerchant.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.bisamerchant.data.MerchantRepository
import com.bangkit.bisamerchant.data.response.Merchant
import com.google.firebase.firestore.ListenerRegistration

class MerchantViewModel(private val repository: MerchantRepository) : ViewModel() {
    private val _merchant = MutableLiveData<Merchant>()
    val merchant: LiveData<Merchant> get() = _merchant

    private var listenerRegistration: ListenerRegistration? = null

    fun observeMerchantActive() {
        listenerRegistration = repository.observeMerchantActive { merchant ->
            _merchant.value = merchant
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