package com.bangkit.bisamerchant.domain.splash.repository

import kotlinx.coroutines.flow.Flow

interface ISplashRepository {
    suspend fun getMerchantActive(): Flow<String>
}