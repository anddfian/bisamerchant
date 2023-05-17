package com.bangkit.bisamerchant.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.bisamerchant.data.TransactionRepository
import com.bangkit.bisamerchant.di.Injection
import com.bangkit.bisamerchant.ui.home.TransactionViewModel
import com.bangkit.bisamerchant.ui.invoice.DetailTransactionViewModel

class ViewModelTransactionFactory constructor(private val transactionRepository: TransactionRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(transactionRepository) as T
        } else if (modelClass.isAssignableFrom(DetailTransactionViewModel::class.java)) {
            return DetailTransactionViewModel(transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelTransactionFactory? = null

        fun getInstance(pref: MerchantPreferences): ViewModelTransactionFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: ViewModelTransactionFactory(Injection.provideTransactionRepository(pref))
            }.also { INSTANCE = it }
    }
}