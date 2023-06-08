package com.bangkit.bisamerchant.data.merchantsetting.repository

import android.net.Uri
import com.bangkit.bisamerchant.data.merchantsetting.datasource.MerchantSettingDataSource
import com.bangkit.bisamerchant.domain.merchantsetting.repository.IMerchantSettingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MerchantSettingRepository @Inject constructor(
    private val merchantSettingDataSource: MerchantSettingDataSource
) : IMerchantSettingRepository {
    override suspend fun getMerchantActive() =
        merchantSettingDataSource.getMerchantActive()

    override suspend fun updateMerchantInfo(name: String, address: String, type: String, newPhoto: Uri?) =
        merchantSettingDataSource.updateMerchantActive(name, address, type, newPhoto)
}