package com.bangkit.bisamerchant.data.di

import com.bangkit.bisamerchant.data.history.repository.HistoryRepository
import com.bangkit.bisamerchant.data.home.repository.HomeRepository
import com.bangkit.bisamerchant.data.invoice.repository.InvoiceRepository
import com.bangkit.bisamerchant.data.login.repository.LoginRepository
import com.bangkit.bisamerchant.data.merchantsetting.repository.MerchantSettingRepository
import com.bangkit.bisamerchant.data.pin.repository.PinRepository
import com.bangkit.bisamerchant.data.profile.repository.ProfileRepository
import com.bangkit.bisamerchant.data.register.repository.RegisterRepository
import com.bangkit.bisamerchant.data.setting.repository.SettingRepository
import com.bangkit.bisamerchant.domain.history.repository.IHistoryRepository
import com.bangkit.bisamerchant.domain.home.repository.IHomeRepository
import com.bangkit.bisamerchant.domain.invoice.repository.IInvoiceRepository
import com.bangkit.bisamerchant.domain.login.repository.ILoginRepository
import com.bangkit.bisamerchant.domain.merchantsetting.repository.IMerchantSettingRepository
import com.bangkit.bisamerchant.domain.pin.repository.IPinRepository
import com.bangkit.bisamerchant.domain.profile.repository.IProfileRepository
import com.bangkit.bisamerchant.domain.register.repository.IRegisterRepository
import com.bangkit.bisamerchant.domain.setting.repository.ISettingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [FirebaseModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideHomeRepository(homeRepository: HomeRepository): IHomeRepository

    @Binds
    abstract fun provideHistoryRepository(historyRepository: HistoryRepository): IHistoryRepository

    @Binds
    abstract fun provideInvoiceRepository(invoiceRepository: InvoiceRepository): IInvoiceRepository

    @Binds
    abstract fun provideMerchantSettingRepository(merchantSettingRepository: MerchantSettingRepository): IMerchantSettingRepository

    @Binds
    abstract fun provideSettingRepository(settingRepository: SettingRepository): ISettingRepository

    @Binds
    abstract fun provideProfileRepository(profileRepository: ProfileRepository): IProfileRepository

    @Binds
    abstract fun providePinRepository(pinRepository: PinRepository): IPinRepository

    @Binds
    abstract fun provideRegisterRepository(registerRepository: RegisterRepository): IRegisterRepository

    @Binds
    abstract fun provideLoginRepository(loginRepository: LoginRepository): ILoginRepository
}