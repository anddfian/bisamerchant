package com.bangkit.bisamerchant.data.splash.repository

import com.bangkit.bisamerchant.data.splash.datasource.SplashDatasource
import com.bangkit.bisamerchant.domain.splash.repository.ISplashRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SplashRepository @Inject constructor(
    private val splashDatasource: SplashDatasource
) : ISplashRepository {
    override suspend fun getMerchantActive() =
        splashDatasource.getMerchantActive()
}