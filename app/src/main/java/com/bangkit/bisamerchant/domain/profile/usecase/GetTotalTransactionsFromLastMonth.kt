package com.bangkit.bisamerchant.domain.profile.usecase

import com.bangkit.bisamerchant.domain.profile.repository.IProfileRepository
import javax.inject.Inject

class GetTotalTransactionsFromLastMonth @Inject constructor(private val profileRepository: IProfileRepository) {

    suspend fun execute() =
        profileRepository.getTotalTransactionsFromLastMonth()
}