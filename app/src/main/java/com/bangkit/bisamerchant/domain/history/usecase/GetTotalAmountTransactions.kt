package com.bangkit.bisamerchant.domain.history.usecase

import com.bangkit.bisamerchant.domain.history.model.Transaction
import com.bangkit.bisamerchant.domain.history.repository.IHistoryRepository

import javax.inject.Inject

class GetTotalAmountTransactions @Inject constructor(private val historyRepository: IHistoryRepository) {

    fun execute(listTransactions: List<Transaction>) =
        historyRepository.getTotalAmountTransactions(listTransactions)
}