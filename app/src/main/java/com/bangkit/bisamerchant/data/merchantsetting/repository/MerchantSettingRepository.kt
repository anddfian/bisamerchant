package com.bangkit.bisamerchant.data.merchantsetting.repository

import com.bangkit.bisamerchant.data.merchantsetting.datasource.MerchantSettingDataSource
import com.bangkit.bisamerchant.domain.merchantsetting.model.Merchant
import com.bangkit.bisamerchant.domain.merchantsetting.repository.IMerchantSettingRepository
import com.google.firebase.firestore.ListenerRegistration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MerchantSettingRepository @Inject constructor(
    private val merchantSettingDataSource: MerchantSettingDataSource
) : IMerchantSettingRepository {
    override suspend fun getMerchantActive(callback: (Merchant) -> Unit): ListenerRegistration =
        merchantSettingDataSource.getMerchantActive(callback)
}