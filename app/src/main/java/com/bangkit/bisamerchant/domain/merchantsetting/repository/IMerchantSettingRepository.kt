package com.bangkit.bisamerchant.domain.merchantsetting.repository

import android.net.Uri
import com.bangkit.bisamerchant.domain.merchantsetting.model.Merchant
import kotlinx.coroutines.flow.Flow

interface IMerchantSettingRepository {
    suspend fun getMerchantActive(): Flow<Merchant>
    suspend fun updateMerchantInfo(name: String, address: String, type: String, newPhoto: Uri?): Flow<String>
}