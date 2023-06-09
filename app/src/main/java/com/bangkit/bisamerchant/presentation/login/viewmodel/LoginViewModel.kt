package com.bangkit.bisamerchant.presentation.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.domain.login.usecase.Login
import com.bangkit.bisamerchant.domain.login.usecase.ResetPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: Login,
    private val resetPassword: ResetPassword,
) : ViewModel() {

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            login.execute(email, password)
                .onStart {
                    _isLoading.value = true
                }.catch { e ->
                    _message.value = e.message.toString()
                    _isLoading.value = false
                }.collect { result ->
                    _isLoading.value = false
                    _message.value = result
                }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            resetPassword.execute(email).onStart {
                    _isLoading.value = true
                }.catch { e ->
                    _message.value = e.message.toString()
                    _isLoading.value = false
                    _message.value = null
                }.collect { result ->
                    _isLoading.value = false
                    _message.value = result
                }
        }
    }
}