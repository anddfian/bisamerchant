package com.bangkit.bisamerchant.presentation.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.domain.profile.model.Merchant
import com.bangkit.bisamerchant.domain.profile.usecase.GetMerchantActive
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMerchantActive: GetMerchantActive
) : ViewModel() {

    private val _merchant = MutableLiveData<Merchant>()
    val merchant: LiveData<Merchant> get() = _merchant

    private var listenerRegistration: ListenerRegistration? = null

    fun getMerchantActive() {
        viewModelScope.launch {
            listenerRegistration = getMerchantActive.execute { merchant ->
                _merchant.value = merchant
            }
        }
    }
}