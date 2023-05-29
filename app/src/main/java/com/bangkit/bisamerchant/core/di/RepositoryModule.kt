package com.bangkit.bisamerchant.core.di

import com.bangkit.bisamerchant.core.data.MerchantRepository
import com.bangkit.bisamerchant.core.data.TransactionRepository
import com.bangkit.bisamerchant.core.domain.repository.IMerchantRepository
import com.bangkit.bisamerchant.core.domain.repository.ITransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [FirebaseModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideMerchantRepository(merchantRepository: MerchantRepository): IMerchantRepository

    @Binds
    abstract fun provideTransactionRepository(transactionRepository: TransactionRepository): ITransactionRepository
}