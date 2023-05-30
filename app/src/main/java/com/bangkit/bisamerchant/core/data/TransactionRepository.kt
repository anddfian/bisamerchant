package com.bangkit.bisamerchant.core.data

import com.bangkit.bisamerchant.core.domain.model.DetailTransaction
import com.bangkit.bisamerchant.core.domain.model.Payment
import com.bangkit.bisamerchant.core.domain.model.Transaction
import com.bangkit.bisamerchant.core.domain.repository.ITransactionRepository
import com.bangkit.bisamerchant.core.helper.SharedPreferences
import com.bangkit.bisamerchant.core.helper.Utils
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val pref: SharedPreferences,
    private val db: FirebaseFirestore,
) : ITransactionRepository {
    private var listenerRegistration: ListenerRegistration? = null

    override suspend fun addTransaction(
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

                    transactionDocument.document(newTransactionId).set(transaction)
                        .addOnSuccessListener {
                            deferredMessage.complete("Transaksi berhasil")
                        }.addOnFailureListener { e ->
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

    override suspend fun getPayerBalance(payerId: String): Long? = withContext(Dispatchers.IO) {
        val documentSnapshot = db.collection("user").document(payerId).get().await()
        return@withContext documentSnapshot.getLong("balance")
    }

    override suspend fun observeTransactionsToday(callback: (List<Transaction>) -> Unit): ListenerRegistration {
        return withContext(Dispatchers.IO) {
            val merchantId = getMerchantId()
            val timestampToday = Utils.getTodayTimestamp()
            val query = db.collection("transaction").whereEqualTo("merchantId", merchantId)
                .whereGreaterThanOrEqualTo("timestamp", timestampToday)
                .orderBy("timestamp", Query.Direction.DESCENDING)

            listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let {
                    val transactions = processTransactionQuerySnapshot(it)
                    callback(transactions)
                }
            }

            return@withContext listenerRegistration as ListenerRegistration
        }
    }

    override fun processTransactionQuerySnapshot(querySnapshot: QuerySnapshot): List<Transaction> {
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

    override fun observeTransactions(callback: (List<Transaction>) -> Unit): ListenerRegistration {
        val merchantId = runBlocking { pref.getMerchantId().first() }
        val query = db.collection("transaction").whereEqualTo("merchantId", merchantId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
            querySnapshot?.let {
                val transactions = processTransactionQuerySnapshot(it)
                callback(transactions)
            }
        }

        return listenerRegistration as ListenerRegistration
    }

    override suspend fun observeTransactionsWithFilter(
        queryDirection: Query.Direction?,
        startDate: Long?,
        endDate: Long?,
        trxType: String?,
        callback: (List<Transaction>) -> Unit
    ): ListenerRegistration {
        val direction = queryDirection ?: Query.Direction.ASCENDING
        val merchantId = getMerchantId()
        val start = startDate ?: 0

        val dayMidnight = 86399000L
        val end = endDate?.plus(dayMidnight) ?: Utils.getTodayTimestamp().plus(dayMidnight)
        withContext(Dispatchers.IO) {
            val query = if (trxType != null) {
                db.collection("transaction")
                    .whereEqualTo("merchantId", merchantId)
                    .whereEqualTo("trxType", trxType)
                    .whereGreaterThanOrEqualTo("timestamp", start)
                    .whereLessThanOrEqualTo("timestamp", end)
                    .orderBy("timestamp", direction)
            } else {
                db.collection("transaction")
                    .whereEqualTo("merchantId", merchantId)
                    .whereGreaterThanOrEqualTo("timestamp", start)
                    .whereLessThanOrEqualTo("timestamp", end)
                    .orderBy("timestamp", direction)
            }

            listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let {
                    val transactions = processTransactionQuerySnapshot(it)
                    callback(transactions)
                }
            }
        }


        return listenerRegistration as ListenerRegistration
    }

    override suspend fun getTransactionById(id: String): DocumentSnapshot =
        withContext(Dispatchers.IO) {
            return@withContext db.collection("transaction").document(id).get().await()
        }

    override fun processTransactionDocumentSnapshot(documentSnapshot: DocumentSnapshot): DetailTransaction {
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

    override fun getTotalAmountTransactions(listTransactions: List<Transaction>): Long {
        return listTransactions.fold(0L) { totalAmount, transaction ->
            if (transaction.trxType == "PAYMENT") {
                totalAmount + transaction.amount
            } else {
                totalAmount - transaction.amount
            }
        }
    }

    override fun getMerchantId() = runBlocking {
        pref.getMerchantId().first()
    }

    override fun getTransactionCount() = runBlocking {
        pref.getTransactionCount().first()
    }

    override fun saveTransactionCount(count: Int) = runBlocking {
        pref.saveTransactionCount(count)
    }


    override fun stopObserving() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}