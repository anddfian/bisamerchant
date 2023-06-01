package com.bangkit.bisamerchant.data.merchantregister.repository

import android.net.Uri
import com.bangkit.bisamerchant.data.merchantregister.datasource.MerchantRegisterDataSource
import com.bangkit.bisamerchant.domain.merchantregister.repository.IMerchantRegisterRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MerchantRegisterRepository @Inject constructor(
    private val merchantRegisterDataSource: MerchantRegisterDataSource
) :
    IMerchantRegisterRepository {
    override suspend fun addMerchant(
        name: String,
        address: String,
        type: String,
        photo: Uri,
    ) =
        merchantRegisterDataSource.addMerchant(name, address, type, photo)
}