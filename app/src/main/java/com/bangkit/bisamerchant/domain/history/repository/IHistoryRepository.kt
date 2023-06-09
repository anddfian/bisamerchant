package com.bangkit.bisamerchant.domain.history.repository

import com.bangkit.bisamerchant.domain.history.model.FilteredTransaction
import com.bangkit.bisamerchant.domain.history.model.Transaction
import kotlinx.coroutines.flow.Flow

interface IHistoryRepository {
    suspend fun getTransactions(): Flow<MutableList<Transaction>>
    suspend fun getTransactionsWithFilter(filteredTransaction: FilteredTransaction): Flow<MutableList<Transaction>>
    suspend fun getTotalAmountTransactions(listTransactions: List<Transaction>): Long
}