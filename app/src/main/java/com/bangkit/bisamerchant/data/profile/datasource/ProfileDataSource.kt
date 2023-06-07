package com.bangkit.bisamerchant.data.profile.datasource

import com.bangkit.bisamerchant.data.utils.SharedPreferences
import com.bangkit.bisamerchant.data.utils.Utils
import com.bangkit.bisamerchant.domain.profile.model.Merchant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileDataSource @Inject constructor(
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
                            pref.saveMerchantId(document.id)
                        }
                        val id = document.id
                        val balance = document.getLong("balance")
                        val merchantActive = document.getBoolean("merchantActive")
                        val merchantLogo = document.getString("merchantLogo")
                        val merchantAddress = document.getString("merchantAddress")
                        val merchantType = document.getString("merchantType")
                        val email = document.getString("email")
                        val merchantName = document.getString("merchantName")

                        data = Merchant(
                            id,
                            balance,
                            merchantActive,
                            merchantLogo,
                            merchantAddress,
                            merchantType,
                            email,
                            merchantName,
                        )
                    }

                    callback(data)
                }
            }

            return@withContext listenerRegistration
        }
    }

    suspend fun getTotalTransactionsFromLastMonth(): Flow<Long> = flow {
        val startOfLastMonthTimestamp = Utils.getStartOfLastMonthTimestamp()

        val merchantId = pref.getMerchantId().first()

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
        emit(totalAmountTransactionLastMonthUntilToday)
    }
}