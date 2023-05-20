package com.bangkit.bisamerchant.data

import android.util.Log
import com.bangkit.bisamerchant.data.response.DetailTransaction
import com.bangkit.bisamerchant.data.response.Payment
import com.bangkit.bisamerchant.data.response.Transaction
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.bangkit.bisamerchant.helper.Utils
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TransactionRepository(
    private val pref: MerchantPreferences
) {
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    suspend fun addTransaction(
        payment: Payment
    ): String? {
        val deferredMessage = CompletableDeferred<String>()
        val transactionDocument = db.collection("transaction")
        val newTransactionId = transactionDocument.document().id
        val currentBalance = getPayerBalance(payment.payerId)
        if (currentBalance != null) {
            if (currentBalance > payment.amount) {
                withContext(Dispatchers.IO) {
                    val transaction = hashMapOf(
                        "amount" to payment.amount,
                        "merchantId" to payment.merchantId,
                        "payerId" to payment.payerId,
                        "id" to newTransactionId,
                        "timestamp" to payment.timestamp,
                        "trxType" to payment.trxType
                    )

                    transactionDocument
                        .document(newTransactionId)
                        .set(transaction)
                        .addOnSuccessListener {
                            deferredMessage.complete("Transaksi berhasil")
                        }
                        .addOnFailureListener { e ->
                            deferredMessage.completeExceptionally(e)
                        }
                }
            }
        }

        return try {
            deferredMessage.await()
        } catch (e: Exception) {
            e.message
        }
    }

    private suspend fun getPayerBalance(payerId: String): Long? = withContext(Dispatchers.IO) {
        val documentSnapshot = db.collection("user").document(payerId).get().await()
        return@withContext documentSnapshot.getLong("balance")
    }

    fun observeTransactionsToday(callback: (List<Transaction>) -> Unit): ListenerRegistration {
        val merchantId = runBlocking { pref.getMerchantId().first() }
        val timestampToday = Utils.getTodayTimestamp()
        val query = db.collection("transaction")
            .whereEqualTo("merchantId", merchantId)
            .whereGreaterThanOrEqualTo("timestamp", timestampToday)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
            querySnapshot?.let {
                val transactions = processTransactionQuerySnapshot(it)
                callback(transactions)
            }
        }

        return listenerRegistration as ListenerRegistration
    }

    fun stopObserving() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }

    private fun processTransactionQuerySnapshot(querySnapshot: QuerySnapshot): List<Transaction> {
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

    fun observeTransactions(callback: (List<Transaction>) -> Unit): ListenerRegistration {
        val merchantId = runBlocking { pref.getMerchantId().first() }

        val query = db.collection("transaction")
            .whereEqualTo("merchantId", merchantId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
            querySnapshot?.let {
                val transactions = processTransactionQuerySnapshot(it)
                callback(transactions)
            }
        }

        return listenerRegistration as ListenerRegistration
    }

    fun observeTransactionsWithFilter(
        queryDirection: Query.Direction,
        callback: (List<Transaction>) -> Unit,
    ): ListenerRegistration {
        val merchantId = runBlocking { pref.getMerchantId().first() }
        val query = db.collection("transaction")
            .whereEqualTo("merchantId", merchantId)
            .orderBy("timestamp", queryDirection)

        listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
            querySnapshot?.let {
                val transactions = processTransactionQuerySnapshot(it)
                callback(transactions)
            }
        }

        return listenerRegistration as ListenerRegistration
    }

    fun observeTransactionsWithFilter(
        queryDirection: Query.Direction,
        trxType: String,
        callback: (List<Transaction>) -> Unit,
    ): ListenerRegistration {
        val merchantId = runBlocking { pref.getMerchantId().first() }
        val query = db.collection("transaction")
            .whereEqualTo("merchantId", merchantId)
            .whereEqualTo("trxType", trxType)
            .orderBy("timestamp", queryDirection)

        listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
            querySnapshot?.let {
                val transactions = processTransactionQuerySnapshot(it)
                callback(transactions)
            }
        }

        return listenerRegistration as ListenerRegistration
    }

    fun observeTransactionsWithFilter(
        queryDirection: Query.Direction,
        startDate: Long,
        endDate: Long,
        callback: (List<Transaction>) -> Unit,
    ): ListenerRegistration {
        val dayMidnight = 86399000L
        val endDateNew = endDate + dayMidnight
        val merchantId = runBlocking { pref.getMerchantId().first() }
        val query = db.collection("transaction")
            .whereEqualTo("merchantId", merchantId)
            .whereGreaterThanOrEqualTo("timestamp", startDate)
            .whereLessThanOrEqualTo("timestamp", endDateNew)
            .orderBy("timestamp", queryDirection)

        listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
            querySnapshot?.let {
                val transactions = processTransactionQuerySnapshot(it)
                callback(transactions)
            }
        }

        return listenerRegistration as ListenerRegistration
    }

    fun observeTransactionsWithFilter(
        queryDirection: Query.Direction,
        startDate: Long,
        endDate: Long,
        trxType: String,
        callback: (List<Transaction>) -> Unit,
    ): ListenerRegistration {
        val dayMidnight = 86399000L
        val endDateNew = endDate + dayMidnight
        val merchantId = runBlocking { pref.getMerchantId().first() }
        val query = db.collection("transaction")
            .whereEqualTo("merchantId", merchantId)
            .whereGreaterThanOrEqualTo("timestamp", startDate)
            .whereLessThanOrEqualTo("timestamp", endDateNew)
            .whereEqualTo("trxType", trxType)
            .orderBy("timestamp", queryDirection)

        listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
            querySnapshot?.let {
                val transactions = processTransactionQuerySnapshot(it)
                callback(transactions)
            }
        }

        return listenerRegistration as ListenerRegistration
    }

    suspend fun getTransactionById(id: String): DocumentSnapshot = withContext(Dispatchers.IO) {
        return@withContext db.collection("transaction").document(id).get().await()
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
        return listTransactions.fold(0L) { totalAmount, transaction ->
            if (transaction.trxType == "PAYMENT") {
                totalAmount + transaction.amount
            } else {
                totalAmount - transaction.amount
            }
        }
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