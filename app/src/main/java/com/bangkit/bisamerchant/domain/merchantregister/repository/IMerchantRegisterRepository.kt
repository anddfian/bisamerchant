package com.bangkit.bisamerchant.domain.merchantregister.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface IMerchantRegisterRepository {
    suspend fun addMerchant(name: String, address: String, type: String, photo: Uri): Flow<String>
}