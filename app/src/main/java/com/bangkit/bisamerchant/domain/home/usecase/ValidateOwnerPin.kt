package com.bangkit.bisamerchant.domain.home.usecase

import com.bangkit.bisamerchant.domain.home.repository.IHomeRepository
import javax.inject.Inject

class ValidateOwnerPin @Inject constructor(private val homeRepository: IHomeRepository) {

    suspend fun execute(inputPin: Int) =
        homeRepository.validateOwnerPin(inputPin)
}