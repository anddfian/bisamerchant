package com.bangkit.bisamerchant.data

import com.bangkit.bisamerchant.data.response.Transaction
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.bangkit.bisamerchant.helper.Utils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TransactionRepository(
    private val pref: MerchantPreferences
) {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getTransactions(): QuerySnapshot = withContext(Dispatchers.IO) {
        val merchantId = runBlocking { pref.getMerchantId().first() }

        return@withContext db.collection("transaction").whereEqualTo("merchantId", merchantId).get()
            .await()
    }

    suspend fun getTransactionsToday(): QuerySnapshot = withContext(Dispatchers.IO) {
        val merchantId = runBlocking { pref.getMerchantId().first() }
        val timestampToday = Utils.getTodayTimestamp()

        return@withContext db.collection("transaction").whereEqualTo("merchantId", merchantId)
            .whereGreaterThanOrEqualTo("timestamp", timestampToday)
            .orderBy("timestamp", Query.Direction.DESCENDING).get().await()
    }

    fun processTransactionQuerySnapshot(querySnapshot: QuerySnapshot): List<Transaction> {
        val data = mutableListOf<Transaction>()

        for (document in querySnapshot.documents) {
            val amount = document.getLong("amount")
            val trxType = document.getString("trxType")
            val id = document.id
            val timestamp = document.getLong("timestamp")

            if (amount != null) {
                data.add(
                    Transaction(
                        amount, trxType, id, timestamp
                    )
                )
            }
        }

        return data
    }

    fun getTotalDailyTransactions(listTransactions: List<Transaction>): Long {
        return listTransactions.sumOf { it.amount }
    }

    companion object {
        @Volatile
        private var instance: TransactionRepository? = null
        fun getInstance(pref: MerchantPreferences): TransactionRepository =
            instance ?: synchronized(this) {
                instance ?: TransactionRepository(pref)
            }.also { instance = it }
    }
}