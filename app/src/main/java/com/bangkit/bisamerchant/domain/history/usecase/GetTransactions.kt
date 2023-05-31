package com.bangkit.bisamerchant.domain.history.usecase

import com.bangkit.bisamerchant.domain.history.model.Transaction
import com.bangkit.bisamerchant.domain.history.repository.IHistoryRepository
import javax.inject.Inject

class GetTransactions @Inject constructor(private val historyRepository: IHistoryRepository){

    suspend fun execute(callback: (List<Transaction>) -> Unit) =
        historyRepository.getTransactions(callback)
}