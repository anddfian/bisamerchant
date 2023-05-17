package com.bangkit.bisamerchant.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.bisamerchant.data.MerchantRepository
import com.bangkit.bisamerchant.di.Injection
import com.bangkit.bisamerchant.ui.home.MerchantViewModel

class ViewModelMerchantFactory constructor(private val merchantRepository: MerchantRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MerchantViewModel::class.java)) {
            return MerchantViewModel(merchantRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelMerchantFactory? = null

        fun getInstance(pref: MerchantPreferences): ViewModelMerchantFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelMerchantFactory(Injection.provideMerchantRepository(pref))
            }.also { INSTANCE = it }
    }
}