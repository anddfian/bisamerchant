package com.bangkit.bisamerchant.data.profile.datasource

import com.bangkit.bisamerchant.data.utils.SharedPreferences
import com.bangkit.bisamerchant.data.utils.Utils
import com.bangkit.bisamerchant.domain.profile.model.Merchant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileDataSource @Inject constructor(
    private val pref: SharedPreferences,
    private val db: FirebaseFirestore,
) {
    suspend fun getMerchantActive() = flow {
        try {
            val merchantId = pref.getMerchantId().first()
            val merchant = db.collection("merchant").document(merchantId).get().await()

            merchant.apply {
                emit(
                    Merchant(
                        merchantId,
                        getLong("balance"),
                        getBoolean("merchantActive"),
                        getString("merchantLogo"),
                        getString("merchantAddress"),
                        getString("merchantType"),
                        getString("email"),
                        getString("merchantName"),
                    )
                )
            }
        } catch (e: Exception) {
            throw Exception(e.localizedMessage)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getTotalTransactionsFromLastMonth() = flow {
        val startOfLastMonthTimestamp = Utils.getStartOfLastMonthTimestamp()

        val merchantId = pref.getMerchantId().first()

        try {
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
        } catch (e: Exception) {
            throw Exception(e.localizedMessage)
        }
    }
}