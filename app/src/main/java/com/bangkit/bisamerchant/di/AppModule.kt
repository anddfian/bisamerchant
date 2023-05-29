package com.bangkit.bisamerchant.di

import com.bangkit.bisamerchant.core.domain.usecase.MerchantInteractor
import com.bangkit.bisamerchant.core.domain.usecase.MerchantUseCase
import com.bangkit.bisamerchant.core.domain.usecase.TransactionInteractor
import com.bangkit.bisamerchant.core.domain.usecase.TransactionUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AppModule {
    @Binds
    @ViewModelScoped
    abstract fun provideTransactionUseCase(transactionInteractor: TransactionInteractor): TransactionUseCase

    @Binds
    @ViewModelScoped
    abstract fun provideMerchantUseCase(merchantInteractor: MerchantInteractor): MerchantUseCase
}