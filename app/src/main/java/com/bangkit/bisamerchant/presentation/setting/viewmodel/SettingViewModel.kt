package com.bangkit.bisamerchant.presentation.setting.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.domain.setting.usecase.Logout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val logout: Logout,
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun logout() {
        viewModelScope.launch {
            logout.execute()
                .onStart {
                    _isLoading.value = true
                }
                .catch { e ->
                    _isLoading.value = false
                    _message.value = "${e.message}"
                }
                .collect { result ->
                    _isLoading.value = false
                    _message.value = result
                }
        }
    }
}
