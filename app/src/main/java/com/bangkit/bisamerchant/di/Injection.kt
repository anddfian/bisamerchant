package com.bangkit.bisamerchant.di

import com.bangkit.bisamerchant.data.MerchantRepository
import com.bangkit.bisamerchant.data.TransactionRepository
import com.bangkit.bisamerchant.helper.MerchantPreferences

object Injection {
    fun provideMerchantRepository(pref: MerchantPreferences): MerchantRepository {
        return MerchantRepository.getInstance(pref)
    }
    fun provideTransactionRepository(pref: MerchantPreferences): TransactionRepository {
        return TransactionRepository.getInstance(pref)
    }
}