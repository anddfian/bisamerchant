package com.bangkit.bisamerchant.domain.profile.repository

import com.bangkit.bisamerchant.domain.profile.model.Merchant
import kotlinx.coroutines.flow.Flow

interface IProfileRepository {
    suspend fun getMerchantActive(): Flow<Merchant>
    suspend fun getTotalTransactionsFromLastMonth(): Flow<Long>
}