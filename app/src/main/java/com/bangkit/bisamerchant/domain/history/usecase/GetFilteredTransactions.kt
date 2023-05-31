package com.bangkit.bisamerchant.domain.history.usecase

import com.bangkit.bisamerchant.domain.history.model.FilteredTransaction
import com.bangkit.bisamerchant.domain.history.model.Transaction
import com.bangkit.bisamerchant.domain.history.repository.IHistoryRepository
import javax.inject.Inject

class GetFilteredTransactions @Inject constructor(private val historyRepository: IHistoryRepository) {

    suspend fun execute(filteredTransaction: FilteredTransaction, callback: (List<Transaction>) -> Unit) =
        historyRepository.getTransactionsWithFilter(filteredTransaction, callback)
}