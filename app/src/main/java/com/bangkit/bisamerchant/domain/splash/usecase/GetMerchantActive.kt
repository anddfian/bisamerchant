package com.bangkit.bisamerchant.domain.splash.usecase

import com.bangkit.bisamerchant.domain.splash.repository.ISplashRepository
import javax.inject.Inject

class GetMerchantActive @Inject constructor(private val splashRepository: ISplashRepository) {

    suspend fun execute() =
        splashRepository.getMerchantActive()
}