package com.bangkit.bisamerchant.data.history.repository

import com.bangkit.bisamerchant.data.history.datasource.HistoryDataSource
import com.bangkit.bisamerchant.domain.history.model.FilteredTransaction
import com.bangkit.bisamerchant.domain.history.model.Transaction
import com.bangkit.bisamerchant.domain.history.repository.IHistoryRepository
import com.google.firebase.firestore.ListenerRegistration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(private val historyDataSource: HistoryDataSource) :
    IHistoryRepository {
    override suspend fun getTransactions(callback: (List<Transaction>) -> Unit): ListenerRegistration =
        historyDataSource.getTransactions(callback)

    override suspend fun getTransactionsWithFilter(
        filteredTransaction: FilteredTransaction, callback: (List<Transaction>) -> Unit
    ): ListenerRegistration =
        historyDataSource.getTransactionsWithFilter(filteredTransaction, callback)

    override fun getTotalAmountTransactions(listTransactions: List<Transaction>): Long =
        historyDataSource.getTotalAmountTransactions(listTransactions)
}