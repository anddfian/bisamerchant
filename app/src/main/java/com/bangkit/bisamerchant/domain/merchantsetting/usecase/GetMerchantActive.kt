package com.bangkit.bisamerchant.domain.merchantsetting.usecase

import com.bangkit.bisamerchant.domain.merchantsetting.repository.IMerchantSettingRepository
import javax.inject.Inject

class GetMerchantActive @Inject constructor(private val settingRepository: IMerchantSettingRepository) {

    suspend fun execute() =
        settingRepository.getMerchantActive()
}