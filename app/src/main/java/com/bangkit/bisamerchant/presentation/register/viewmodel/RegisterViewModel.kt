package com.bangkit.bisamerchant.presentation.register.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.domain.register.usecase.RegisterOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerOwner: RegisterOwner
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRegisterSuccess = MutableLiveData<Boolean>()
    val isRegisterSuccess: LiveData<Boolean> get() = _isRegisterSuccess

    fun registerOwner(name: String, email: String, password: String, pin: String) {
        viewModelScope.launch {
            registerOwner.execute(name, email, password, pin)
                .onStart {
                    _isLoading.value = true
                }
                .catch { e ->
                    _message.value = "${e.message}"
                }
                .collect { result ->
                    _isLoading.value = false
                    _message.value = result
                    _isRegisterSuccess.value = result == "Register successful"
                }
        }
    }
}