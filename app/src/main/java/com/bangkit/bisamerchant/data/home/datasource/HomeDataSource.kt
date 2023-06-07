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
                        val tokenId = document.getString("tokenId")

                        data = Merchant(
                            id,
                            balance,
                            merchantActive,
                            merchantLogo,
                            merchantAddress,
                            merchantType,
                            email,
                            merchantName,
                            tokenId,
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
                        val tokenId = document.getString("tokenId")

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
                                tokenId,
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
        newBalance: Long,
        fee: Long,
    ): Flow<String> = flow {
        val transactionDocument = db.collection("transaction")
        val merchantDocument = db.collection("merchant")
        val newTransactionId = transactionDocument.document().id

        try {
            val transaction = if (detailTransaction.trxType == "PAYMENT") {
                hashMapOf(
                    "amount" to detailTransaction.amount + fee,
                    "merchantId" to detailTransaction.merchantId,
                    "payerId" to detailTransaction.payerId,
                    "id" to newTransactionId,
                    "timestamp" to detailTransaction.timestamp,
                    "trxType" to detailTransaction.trxType
                )
            } else {
                hashMapOf(
                    "amount" to detailTransaction.amount + fee,
                    "bankAccountNo" to detailTransaction.bankAccountNo,
                    "bankInst" to detailTransaction.bankInst,
                    "merchantId" to detailTransaction.merchantId,
                    "id" to newTransactionId,
                    "timestamp" to detailTransaction.timestamp,
                    "trxType" to detailTransaction.trxType
                )
            }

            if (detailTransaction.trxType == "MERCHANT_WITHDRAW") {
                val merchant = merchantDocument.document(getMerchantId())
                merchant.update("balance", newBalance).await()
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

    suspend fun validateWithdrawAmount(amount: Long): String {
        val (totalAmountTransactionLastMonthUntilToday, totalWithdrawMoneyThisMonth) = getCountAndAmountTransactionsLastMonth()
        when {
            totalWithdrawMoneyThisMonth > 3
                    && totalAmountTransactionLastMonthUntilToday <= 5000000L -> {
                return FAILED_WITHDRAW_COUNT_MAX_BRONZE_LEVEL
            }

            totalWithdrawMoneyThisMonth > 10
                    && totalAmountTransactionLastMonthUntilToday <= 170000000L -> {
                return FAILED_WITHDRAW_COUNT_MAX_SILVER_LEVEL
            }

            totalWithdrawMoneyThisMonth > 25 -> {
                return FAILED_WITHDRAW_COUNT_MAX_GOLD_LEVEL
            }

            amount > 5000000L
                    && totalWithdrawMoneyThisMonth < 3
                    && totalAmountTransactionLastMonthUntilToday <= 5000000L -> {
                return FAILED_WITHDRAW_MAX_BRONZE_LEVEL
            }

            amount > 170000000L && totalWithdrawMoneyThisMonth < 10
                    && totalAmountTransactionLastMonthUntilToday < 170000000L -> {
                return FAILED_WITHDRAW_MAX_SILVER_LEVEL
            }

            amount > 200000000L && totalWithdrawMoneyThisMonth < 25 -> {
                return FAILED_WITHDRAW_MAX_GOLD_LEVEL
            }

            else -> {
                return AMOUNT_VALIDATED
            }
        }
    }

    suspend fun getCountAndAmountTransactionsLastMonth(): Pair<Long, Int> {
        val startOfMonthTimestamp = Utils.getStartOfMonthTimestamp()
        val startOfLastMonthTimestamp = Utils.getStartOfLastMonthTimestamp()


        val merchantId = getMerchantId()

        val totalTransactionLastMonthUntilToday =
            db.collection("transaction").whereEqualTo("merchantId", merchantId)
                .whereGreaterThanOrEqualTo("timestamp", startOfLastMonthTimestamp)
                .whereEqualTo("trxType", "PAYMENT")
                .get()
                .await()

        var totalAmountTransactionLastMonthUntilToday = 0L
        for (document in totalTransactionLastMonthUntilToday.documents) {
            val transactionAmount = document.getLong("amount") ?: 0L
            totalAmountTransactionLastMonthUntilToday += transactionAmount
        }

        val totalWithdrawMoneyThisMonth =
            db.collection("transaction").whereEqualTo("merchantId", merchantId)
                .whereGreaterThanOrEqualTo("timestamp", startOfMonthTimestamp)
                .whereEqualTo("trxType", "MERCHANT_WITHDRAW")
                .get()
                .await()
                .documents
                .size

        return Pair(totalAmountTransactionLastMonthUntilToday, totalWithdrawMoneyThisMonth)
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

    companion object {
        private const val FAILED_WITHDRAW_MAX_BRONZE_LEVEL =
            "Maksimal penarikan di level Anda adalah 5 juta"
        private const val FAILED_WITHDRAW_COUNT_MAX_BRONZE_LEVEL =
            "Anda hanya dapat menarik dana sebanyak 3 kali dalam satu bulan"
        private const val FAILED_WITHDRAW_MAX_SILVER_LEVEL =
            "Maksimal penarikan di level Anda adalah 170 juta"
        private const val FAILED_WITHDRAW_COUNT_MAX_SILVER_LEVEL =
            "Anda hanya dapat menarik dana sebanyak 10 kali dalam satu bulan"
        private const val FAILED_WITHDRAW_MAX_GOLD_LEVEL =
            "Untuk penarikan > 200 juta hubungi Customer Service"
        private const val FAILED_WITHDRAW_COUNT_MAX_GOLD_LEVEL =
            "Anda hanya dapat menarik dana sebanyak 25 kali dalam satu bulan"
        private const val AMOUNT_VALIDATED = "Silakan masukkan pin"
    }
}