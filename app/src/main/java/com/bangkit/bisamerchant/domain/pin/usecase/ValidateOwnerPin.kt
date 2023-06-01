package com.bangkit.bisamerchant.domain.pin.usecase

import com.bangkit.bisamerchant.domain.pin.repository.IPinRepository
import javax.inject.Inject

class ValidateOwnerPin @Inject constructor(private val pinRepository: IPinRepository) {

    suspend fun execute(inputPin: Int) =
        pinRepository.validateOwnerPin(inputPin)
}