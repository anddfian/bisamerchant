package com.bangkit.bisamerchant.domain.history.repository

import com.bangkit.bisamerchant.domain.history.model.FilteredTransaction
import com.bangkit.bisamerchant.domain.history.model.Transaction
import com.google.firebase.firestore.ListenerRegistration

interface IHistoryRepository {
    suspend fun getTransactions(callback: (List<Transaction>) -> Unit): ListenerRegistration
    suspend fun getTransactionsWithFilter(
        filteredTransaction: FilteredTransaction,
        callback: (List<Transaction>) -> Unit
    ): ListenerRegistration

    fun getTotalAmountTransactions(listTransactions: List<Transaction>): Long
}