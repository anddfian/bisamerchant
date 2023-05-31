package com.bangkit.bisamerchant.domain.home.repository

import com.bangkit.bisamerchant.domain.home.model.Transaction
import com.bangkit.bisamerchant.domain.home.model.Merchant
import com.bangkit.bisamerchant.domain.home.model.Payment
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
    suspend fun getMerchantActive(callback: (Merchant) -> Unit): ListenerRegistration
    suspend fun getMerchants(callback: (List<Merchant>) -> Unit): ListenerRegistration
    suspend fun updateMerchantStatus(id: String?)
    suspend fun updateMerchantId(id: String)
    suspend fun updateHideAmount(hide: Boolean)
    suspend fun getHideAmount(): Boolean
    fun getTotalAmountTransactions(listTransactions: List<Transaction>): Long
    suspend fun deleteMerchant()
    suspend fun postTransaction(payment: Payment): Flow<String>
    suspend fun getTransactionsToday(callback: (List<Transaction>) -> Unit): ListenerRegistration
    suspend fun getMerchantId(): String
    suspend fun getTransactionsCount(): Long
    suspend fun validateOwnerPin(inputPin: Int): Flow<Boolean>
    suspend fun updateTransactionsCount(count: Long)
}