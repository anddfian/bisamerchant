package com.bangkit.bisamerchant.domain.home.usecase

import com.bangkit.bisamerchant.domain.home.repository.IHomeRepository
import javax.inject.Inject

class ValidateWithdrawAmount @Inject constructor(private val homeRepository: IHomeRepository) {

    suspend fun execute(amount: Long) =
        homeRepository.validateWithdrawAmount(amount)
}