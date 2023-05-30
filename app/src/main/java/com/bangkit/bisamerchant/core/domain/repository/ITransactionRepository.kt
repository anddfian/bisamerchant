package com.bangkit.bisamerchant.core.domain.repository

import com.bangkit.bisamerchant.core.domain.model.DetailTransaction
import com.bangkit.bisamerchant.core.domain.model.Payment
import com.bangkit.bisamerchant.core.domain.model.Transaction
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

interface ITransactionRepository {
    suspend fun addTransaction(payment: Payment): String?

    suspend fun getPayerBalance(payerId: String): Long?

    suspend fun observeTransactionsToday(callback: (List<Transaction>) -> Unit): ListenerRegistration

    fun processTransactionQuerySnapshot(querySnapshot: QuerySnapshot): List<Transaction>

    fun observeTransactions(callback: (List<Transaction>) -> Unit): ListenerRegistration

    suspend fun observeTransactionsWithFilter(
        queryDirection: Query.Direction?,
        startDate: Long?,
        endDate: Long?,
        trxType: String?,
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