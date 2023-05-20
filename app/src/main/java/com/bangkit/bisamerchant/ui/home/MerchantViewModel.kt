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

    private val _merchantsList = MutableLiveData<List<Merchant>>()
    val merchantsList: LiveData<List<Merchant>> get() = _merchantsList

    private var listenerRegistration: ListenerRegistration? = null

    fun observeMerchantActive() {
        listenerRegistration = repository.observeMerchantActive { merchant ->
            _merchant.value = merchant
        }
    }

    fun observeMerchants() {
        listenerRegistration = repository.observeMerchants { merchants ->
            _merchantsList.value = merchants
        }
    }

    fun changeMerchantStatus(id: String?) {
        repository.changeMerchantStatus(id)
    }

    fun stopObserving() {
        repository.stopObserving()
    }

    fun deleteMerchant() {
        repository.deleteMerchant()
    }

    fun saveMerchant(id: String) {
        repository.saveMerchantId(id)
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopObserving()
    }
}