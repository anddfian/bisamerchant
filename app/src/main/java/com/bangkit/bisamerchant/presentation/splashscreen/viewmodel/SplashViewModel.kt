package com.bangkit.bisamerchant.presentation.splashscreen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.domain.splash.usecase.GetAuthInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getAuthInfo: GetAuthInfo,
): ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    fun getAuthInfo() {
        viewModelScope.launch {
            getAuthInfo.execute()
                .onStart {}
                .catch { e ->
                    _message.value = "${e.message}"
                }
                .collect { result ->
                    _message.value = result
                }
        }
    }
}