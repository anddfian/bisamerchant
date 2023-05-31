package com.bangkit.bisamerchant.domain.home.usecase

import com.bangkit.bisamerchant.domain.home.model.Transaction
import com.bangkit.bisamerchant.domain.home.repository.IHomeRepository
import javax.inject.Inject

class GetTransactionsToday @Inject constructor(private val homeRepository: IHomeRepository) {

    suspend fun execute(callback: (List<Transaction>) -> Unit) =
        homeRepository.getTransactionsToday(callback)
}