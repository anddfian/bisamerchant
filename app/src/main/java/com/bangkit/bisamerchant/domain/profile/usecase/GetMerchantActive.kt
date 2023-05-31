package com.bangkit.bisamerchant.domain.profile.usecase

import com.bangkit.bisamerchant.domain.profile.model.Merchant
import com.bangkit.bisamerchant.domain.profile.repository.IProfileRepository
import javax.inject.Inject

class GetMerchantActive @Inject constructor(private val profileRepository: IProfileRepository) {

    suspend fun execute(callback: (Merchant) -> Unit) =
        profileRepository.getMerchantActive(callback)
}