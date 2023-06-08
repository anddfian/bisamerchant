package com.bangkit.bisamerchant.data.history.repository

import com.bangkit.bisamerchant.data.history.datasource.HistoryDataSource
import com.bangkit.bisamerchant.domain.history.model.FilteredTransaction
import com.bangkit.bisamerchant.domain.history.model.Transaction
import com.bangkit.bisamerchant.domain.history.repository.IHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(private val historyDataSource: HistoryDataSource) :
    IHistoryRepository {
    override suspend fun getTransactions() =
        historyDataSource.getTransactions()

    override suspend fun getTransactionsWithFilter(filteredTransaction: FilteredTransaction) =
        historyDataSource.getTransactionsWithFilter(filteredTransaction)

    override suspend fun getTotalAmountTransactions(listTransactions: List<Transaction>) =
        listTransactions.fold(0L) { totalAmount, transaction ->
            if (transaction.trxType == "PAYMENT") {
                totalAmount + transaction.amount
            } else {
                totalAmount - transaction.amount
            }
        }

}