package com.bangkit.bisamerchant.presentation.merchantsetting.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.domain.merchantsetting.model.Merchant
import com.bangkit.bisamerchant.domain.merchantsetting.usecase.GetMerchantActive
import com.bangkit.bisamerchant.domain.merchantsetting.usecase.UpdateMerchantInfo
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MerchantSettingViewModel @Inject constructor(
    private val getMerchantActive: GetMerchantActive,
    private val updateMerchantInfo: UpdateMerchantInfo,
) : ViewModel() {

    private val _merchant = MutableLiveData<Merchant>()
    val merchant: LiveData<Merchant> get() = _merchant

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

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

    fun updateMerchantInfo(name: String, address: String, type: String, newPhoto: Uri?) {
        viewModelScope.launch {
            updateMerchantInfo.execute(name, address, type, newPhoto)
                .onStart {
                    _isLoading.value = true
                }
                .catch { e ->
                    _message.value = "Terjadi kesalahan: ${e.message}"
                }
                .collect { result ->
                    _isLoading.value = false
                    _message.value = result
                }
        }
    }
}