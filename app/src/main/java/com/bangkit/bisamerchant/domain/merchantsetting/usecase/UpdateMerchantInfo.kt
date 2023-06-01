package com.bangkit.bisamerchant.domain.merchantsetting.usecase

import android.net.Uri
import com.bangkit.bisamerchant.domain.merchantsetting.repository.IMerchantSettingRepository
import javax.inject.Inject

class UpdateMerchantInfo @Inject constructor(
    private val merchantRepository: IMerchantSettingRepository
){

    suspend fun execute(name: String, address: String, type: String, newPhoto: Uri?) =
        merchantRepository.updateMerchantInfo(name, address, type, newPhoto)
}