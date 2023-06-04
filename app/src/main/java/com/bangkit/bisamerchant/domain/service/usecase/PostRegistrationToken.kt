package com.bangkit.bisamerchant.domain.service.usecase

import com.bangkit.bisamerchant.domain.service.repository.IServiceRepository
import javax.inject.Inject

class PostRegistrationToken @Inject constructor(private val serviceRepository: IServiceRepository) {

    suspend fun execute(token: String) =
        serviceRepository.postRegistrationToken(token)
}