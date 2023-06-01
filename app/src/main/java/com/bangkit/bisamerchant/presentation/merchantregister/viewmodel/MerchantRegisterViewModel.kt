package com.bangkit.bisamerchant.presentation.merchantregister.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.domain.merchantregister.usecase.RegisterMerchant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MerchantRegisterViewModel @Inject constructor(
    private val registerMerchant: RegisterMerchant
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRegisterSuccess = MutableLiveData<Boolean>()
    val isRegisterSuccess: LiveData<Boolean> get() = _isRegisterSuccess

    fun registerMerchant(name: String, address: String, type: String, photo: Uri) {
        viewModelScope.launch {
            registerMerchant.execute(name, address, type, photo)
                .onStart {
                    _isLoading.value = true
                }
                .catch { e ->
                    _message.value = "Terjadi kesalahan: ${e.message}"
                }
                .collect { result ->
                    _isLoading.value = false
                    _message.value = result
                    _isRegisterSuccess.value = result == REGISTER_SUCCESSFUL
                }
        }
    }

    companion object {
        private const val REGISTER_SUCCESSFUL = "Register merchant successful"
    }
}