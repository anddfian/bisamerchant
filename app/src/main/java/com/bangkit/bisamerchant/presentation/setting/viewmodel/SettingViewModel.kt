package com.bangkit.bisamerchant.presentation.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bisamerchant.domain.setting.usecase.Logout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val logout: Logout
) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            logout.execute()
        }
    }
}
