package com.bangkit.bisamerchant.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.core.data.model.Merchant
import com.bangkit.bisamerchant.core.domain.usecase.MerchantUseCase
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MerchantViewModel @Inject constructor(private val merchantUseCase: MerchantUseCase) : ViewModel() {
    private val _merchant = MutableLiveData<Merchant>()
    val merchant: LiveData<Merchant> get() = _merchant

    private val _merchantsList = MutableLiveData<List<Merchant>>()
    val merchantsList: LiveData<List<Merchant>> get() = _merchantsList

    private val _isAmountHide = MutableLiveData<Boolean>()
    val isAmountHide: LiveData<Boolean> get() = _isAmountHide


    private var listenerRegistration: ListenerRegistration? = null

    fun observeMerchantActive() {
        viewModelScope.launch {
            listenerRegistration = merchantUseCase.observeMerchantActive { merchant ->
                _merchant.value = merchant
            }
        }
    }

    fun observeMerchants() {
        viewModelScope.launch {
            listenerRegistration = merchantUseCase.observeMerchants { merchants ->
                _merchantsList.value = merchants
            }
        }
    }

    fun changeMerchantStatus(id: String?) {
        viewModelScope.launch {
            merchantUseCase.changeMerchantStatus(id)
        }
    }

    fun stopObserving() {
        merchantUseCase.stopObserving()
    }

    fun deleteMerchant() {
        merchantUseCase.deleteMerchant()
    }

    fun getMerchantId() =
        merchantUseCase.getMerchantId()

    fun saveMerchant(id: String) {
        merchantUseCase.saveMerchantId(id)
    }

    fun getAmountHide() {
        _isAmountHide.value = merchantUseCase.getAmountHide()
    }

    fun saveAmountHide(hide: Boolean) {
        merchantUseCase.saveAmountHide(hide)
    }

    override fun onCleared() {
        super.onCleared()
        merchantUseCase.stopObserving()
    }
}