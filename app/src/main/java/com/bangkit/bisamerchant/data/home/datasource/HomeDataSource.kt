package com.bangkit.bisamerchant.data.home.datasource

import com.bangkit.bisamerchant.data.utils.SharedPreferences
import com.bangkit.bisamerchant.data.utils.Utils
import com.bangkit.bisamerchant.domain.home.model.DetailTransaction
import com.bangkit.bisamerchant.domain.home.model.Merchant
import com.bangkit.bisamerchant.domain.home.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeDataSource @Inject constructor(
    private val pref: SharedPreferences,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {

    suspend fun getMerchantActive(callback: (Merchant) -> Unit): ListenerRegistration {
        return withContext(Dispatchers.IO) {
            val query = db.collection("merchant").whereEqualTo("merchantActive", true)
                .whereEqualTo("email", auth.currentUser?.email)

            val listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let {
                    var data = Merchant()

                    for (document in querySnapshot.documents) {
                        runBlocking {
                            updateMerchantId(document.id)
                        }
                        val id = document.id
                        val balance = document.getLong("balance")
                        val merchantActive = document.getBoolean("merchantActive")
                        val merchantLogo = document.getString("merchantLogo")
                        val merchantAddress = document.getString("merchantAddress")
                        val merchantType = document.getString("merchantType")
                        val email = document.getString("email")
                        val merchantName = document.getString("merchantName")
                        val transactionCount = document.getLong("transactionCount")

                        data = Merchant(
                            id,
                            balance,
                            merchantActive,
                            merchantLogo,
                            merchantAddress,
                            merchantType,
                            email,
                            merchantName,
                            transactionCount
                        )
                    }

                    callback(data)
                }
            }

            return@withContext listenerRegistration
        }
    }

    suspend fun getMerchants(callback: (List<Merchant>) -> Unit): ListenerRegistration {
        return withContext(Dispatchers.IO) {
            val query = db.collection("merchant").whereEqualTo("email", auth.currentUser?.email)

            val listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let {
                    val data = mutableListOf<Merchant>()

                    for (document in querySnapshot.documents) {
                        val id = document.id
                        val balance = document.getLong("balance")
                        val merchantActive = document.getBoolean("merchantActive")
                        val merchantLogo = document.getString("merchantLogo")
                        val merchantAddress = document.getString("merchantAddress")
                        val merchantType = document.getString("merchantType")
                        val email = document.getString("email")
                        val merchantName = document.getString("merchantName")
                        val transactionCount = document.getLong("transactionCount")

                        data.add(
                            Merchant(
                                id,
                                balance,
                                merchantActive,
                                merchantLogo,
                                merchantAddress,
                                merchantType,
                                email,
                                merchantName,
                                transactionCount
                            )
                        )
                    }

                    callback(data)
                }
            }

            return@withContext listenerRegistration
        }
    }

    suspend fun postTransaction(
        detailTransaction: DetailTransaction,
        newBalance: Long
    ): Flow<String> = flow {
        val transactionDocument = db.collection("transaction")
        val merchantDocument = db.collection("merchant")

        val newTransactionId = transactionDocument.document().id
        try {
            val transaction = if (detailTransaction.trxType == "PAYMENT") {
                hashMapOf(
                    "amount" to detailTransaction.amount,
                    "merchantId" to detailTransaction.merchantId,
                    "payerId" to detailTransaction.payerId,
                    "id" to newTransactionId,
                    "timestamp" to detailTransaction.timestamp,
                    "trxType" to detailTransaction.trxType
                )
            } else {
                hashMapOf(
                    "amount" to detailTransaction.amount,
                    "bankAccountNo" to detailTransaction.bankAccountNo,
                    "bankInst" to detailTransaction.bankInst,
                    "merchantId" to detailTransaction.merchantId,
                    "id" to newTransactionId,
                    "timestamp" to detailTransaction.timestamp,
                    "trxType" to detailTransaction.trxType
                )
            }

            if (detailTransaction.trxType == "MERCHANT_WITHDRAW") {
                merchantDocument.document(getMerchantId()).update("balance", newBalance).await()
            }

            transactionDocument.document(newTransactionId).set(transaction).await()

            emit("Transaksi berhasil")
        } catch (e: Exception) {
            emit(e.message ?: "Terjadi kesalahan saat menambahkan transaksi")
        }
    }.catch { e ->
        emit("Terjadi kesalahan: ${e.message}")
    }.flowOn(Dispatchers.IO)

    suspend fun getPayerBalance(payerId: String): Long? = withContext(Dispatchers.IO) {
        val documentSnapshot = db.collection("user").document(payerId).get().await()
        return@withContext documentSnapshot.getLong("balance")
    }

    suspend fun getMerchantBalance(merchantId: String): Long? = withContext(Dispatchers.IO) {
        val documentSnapshot = db.collection("merchant").document(merchantId).get().await()
        return@withContext documentSnapshot.getLong("balance")
    }

    suspend fun getTransactionsToday(callback: (List<Transaction>) -> Unit): ListenerRegistration {
        return withContext(Dispatchers.IO) {
            val merchantId = getMerchantId()
            val timestampToday = Utils.getTodayTimestamp()
            val query = db.collection("transaction").whereEqualTo("merchantId", merchantId)
                .whereGreaterThanOrEqualTo("timestamp", timestampToday)
                .orderBy("timestamp", Query.Direction.DESCENDING)

            val listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let {
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
                    callback(data)
                }
            }

            return@withContext listenerRegistration
        }
    }

    fun getTotalAmountTransactions(listTransactions: List<Transaction>): Long =
        listTransactions.fold(0L) { totalAmount, transaction ->
            if (transaction.trxType == "PAYMENT") {
                totalAmount + transaction.amount
            } else {
                totalAmount - transaction.amount
            }
        }

    suspend fun updateMerchantStatus(id: String?) = withContext(Dispatchers.IO) {
        val merchantActiveNow = getMerchantId()
        if (id != merchantActiveNow) {
            deleteMerchant()
            id?.let { updateMerchantId(it) }
            val merchantCollection = db.collection("merchant")
            merchantCollection.whereEqualTo("email", auth.currentUser?.email).get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        querySnapshot.forEach {
                            val isActive = it.getBoolean("merchantActive")
                            if (isActive == false && it.id == id) {
                                merchantCollection.document(it.id)
                                    .update("merchantActive", it.id == id)
                            }
                        }
                        merchantCollection.document(merchantActiveNow)
                            .update("merchantActive", false)
                    }
                }
        }
    }

    suspend fun updateMerchantId(id: String) {
        withContext(Dispatchers.IO) {
            pref.saveMerchantId(id)
        }
    }

    suspend fun updateHideAmount(hide: Boolean) {
        withContext(Dispatchers.IO) {
            pref.updateHideAmount(hide)
        }
    }

    suspend fun getHideAmount() = withContext(Dispatchers.IO) {
        pref.getHideAmount().first()
    }

    suspend fun deleteMerchant() {
        withContext(Dispatchers.IO) {
            pref.delete()
        }
    }

    suspend fun getMerchantId() = withContext(Dispatchers.IO) {
        pref.getMerchantId().first()
    }

    suspend fun getTransactionsTodayCount(): Long = withContext(Dispatchers.IO) {
        pref.getTransactionsTodayCount().first()
    }

    suspend fun updateTransactionsTodayCount(count: Long) {
        withContext(Dispatchers.IO) {
            pref.updateTransactionsTodayCount(count)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "Bisa_Channel_01"
        private const val CHANNEL_NAME = "Bisa Merchant Transaction"
    }
}