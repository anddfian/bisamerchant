package com.bangkit.bisamerchant.core.domain.usecase

import com.bangkit.bisamerchant.core.data.model.DetailTransaction
import com.bangkit.bisamerchant.core.data.model.Payment
import com.bangkit.bisamerchant.core.data.model.Transaction
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

interface TransactionUseCase {
    suspend fun addTransaction(payment: Payment): String?
    suspend fun getPayerBalance(payerId: String): Long?
    suspend fun observeTransactionsToday(callback: (List<Transaction>) -> Unit): ListenerRegistration
    fun processTransactionQuerySnapshot(querySnapshot: QuerySnapshot): List<Transaction>
    fun observeTransactions(callback: (List<Transaction>) -> Unit): ListenerRegistration
    fun observeTransactionsWithFilter(
        queryDirection: Query.Direction,
        callback: (List<Transaction>) -> Unit,
    ): ListenerRegistration

    fun observeTransactionsWithFilter(
        queryDirection: Query.Direction,
        trxType: String,
        callback: (List<Transaction>) -> Unit,
    ): ListenerRegistration

    fun observeTransactionsWithFilter(
        queryDirection: Query.Direction,
        startDate: Long,
        endDate: Long,
        callback: (List<Transaction>) -> Unit,
    ): ListenerRegistration

    fun observeTransactionsWithFilter(
        queryDirection: Query.Direction,
        startDate: Long,
        endDate: Long,
        trxType: String,
        callback: (List<Transaction>) -> Unit,
    ): ListenerRegistration

    suspend fun getTransactionById(id: String): DocumentSnapshot
    fun processTransactionDocumentSnapshot(documentSnapshot: DocumentSnapshot): DetailTransaction
    fun getTotalAmountTransactions(listTransactions: List<Transaction>): Long
    fun getMerchantId(): String
    fun getTransactionCount(): Int
    fun saveTransactionCount(count: Int)
    fun stopObserving()
}