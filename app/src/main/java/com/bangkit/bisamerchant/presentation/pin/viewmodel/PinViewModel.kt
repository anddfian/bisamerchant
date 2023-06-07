package com.bangkit.bisamerchant.presentation.pin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.domain.pin.usecase.ValidateOwnerPin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val validateOwnerPin: ValidateOwnerPin
) : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _isPinValid = MutableLiveData<Boolean?>()
    val isPinValid: LiveData<Boolean?> get() = _isPinValid

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun validateOwnerPin(inputPin: Int) {
        viewModelScope.launch {
            validateOwnerPin.execute(inputPin)
                .onStart {
                    _isLoading.value = true
                }
                .catch { e ->
                    _message.value = "Terjadi kesalahan: ${e.message}"
                    _isLoading.value = false
                }
                .collect { result ->
                    _isLoading.value = false
                    _isPinValid.value = result
                    if (!result) {
                        _isPinValid.value = null
                    }
                }
        }
    }
}