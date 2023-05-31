package com.bangkit.bisamerchant.domain.home.usecase

import com.bangkit.bisamerchant.domain.home.model.Merchant
import com.bangkit.bisamerchant.domain.home.repository.IHomeRepository
import javax.inject.Inject

class GetMerchantActive @Inject constructor(private val homeRepository: IHomeRepository) {

    suspend fun execute(callback: (Merchant) -> Unit) =
        homeRepository.getMerchantActive(callback)
}