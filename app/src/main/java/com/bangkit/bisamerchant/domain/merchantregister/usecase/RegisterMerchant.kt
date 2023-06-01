package com.bangkit.bisamerchant.domain.merchantregister.usecase

import android.net.Uri
import com.bangkit.bisamerchant.domain.merchantregister.repository.IMerchantRegisterRepository
import javax.inject.Inject

class RegisterMerchant @Inject constructor(
    private val merchantRegisterRepository: IMerchantRegisterRepository
) {

    suspend fun execute(name: String, address: String, type: String, photo: Uri) =
        merchantRegisterRepository.addMerchant(name, address, type, photo)
}