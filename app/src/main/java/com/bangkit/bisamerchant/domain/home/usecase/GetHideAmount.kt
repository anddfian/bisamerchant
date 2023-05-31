package com.bangkit.bisamerchant.domain.home.usecase

import com.bangkit.bisamerchant.domain.home.repository.IHomeRepository
import javax.inject.Inject

class GetHideAmount @Inject constructor(private val homeRepository: IHomeRepository) {

    suspend fun execute() =
        homeRepository.getHideAmount()
}