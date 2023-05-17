package com.bangkit.bisamerchant.data

import com.bangkit.bisamerchant.data.response.DetailTransaction
import com.bangkit.bisamerchant.data.response.Transaction
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.bangkit.bisamerchant.helper.Utils
import com.google.firebase.firestore.DocumentSnapshot
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

    suspend fun getTransactionById(id: String): DocumentSnapshot = withContext(Dispatchers.IO) {
        return@withContext db.collection("transaction").document(id).get().await()
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


    fun processTransactionDocumentSnapshot(documentSnapshot: DocumentSnapshot): DetailTransaction {
        val id = documentSnapshot.id
        val amount = documentSnapshot.getLong("amount")
        val merchantId = documentSnapshot.getString("merchantId")
        val trxType = documentSnapshot.getString("trxType")
        val timestamp = documentSnapshot.getLong("timestamp")

        if (trxType == "PAYMENT") {
            val payerId = documentSnapshot.getString("payerId")
            return DetailTransaction(
                id = id,
                amount = amount,
                merchantId = merchantId,
                payerId = payerId,
                trxType = trxType,
                timestamp = timestamp
            )
        }

        val bankAccountNo = documentSnapshot.getLong("bankAccountNo")
        val bankInst = documentSnapshot.getString("bankInst")
        return DetailTransaction(
            id = id,
            amount = amount,
            merchantId = merchantId,
            bankAccountNo = bankAccountNo,
            bankInst = bankInst,
            trxType = trxType,
            timestamp = timestamp
        )
    }

    fun getTotalAmountTransactions(listTransactions: List<Transaction>): Long {
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