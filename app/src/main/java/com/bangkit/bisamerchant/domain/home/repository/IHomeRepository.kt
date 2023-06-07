package com.bangkit.bisamerchant.domain.home.repository

import com.bangkit.bisamerchant.domain.home.model.DetailTransaction
import com.bangkit.bisamerchant.domain.home.model.Transaction
import com.bangkit.bisamerchant.domain.home.model.Merchant
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
    suspend fun postTransaction(detailTransaction: DetailTransaction): Flow<String>
    suspend fun getTransactionsToday(callback: (List<Transaction>) -> Unit): ListenerRegistration
    suspend fun getMerchantId(): String
    suspend fun validateWithdrawAmount(amount: Long): Flow<String>
    suspend fun getTransactionFee(amount: Long): Flow<Long>
}