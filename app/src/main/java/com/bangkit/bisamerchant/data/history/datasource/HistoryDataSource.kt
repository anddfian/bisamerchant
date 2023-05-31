package com.bangkit.bisamerchant.data.history.datasource

import com.bangkit.bisamerchant.data.utils.SharedPreferences
import com.bangkit.bisamerchant.data.utils.Utils
import com.bangkit.bisamerchant.domain.history.model.FilteredTransaction
import com.bangkit.bisamerchant.domain.history.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryDataSource @Inject constructor(
    private val pref: SharedPreferences,
    private val db: FirebaseFirestore,
) {

    suspend fun getTransactions(callback: (List<Transaction>) -> Unit): ListenerRegistration {
        val merchantId = pref.getMerchantId().first()
        val query = db.collection("transaction").whereEqualTo("merchantId", merchantId)
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

        return listenerRegistration
    }

    suspend fun getTransactionsWithFilter(
        filteredTransaction: FilteredTransaction,
        callback: (List<Transaction>) -> Unit
    ): ListenerRegistration =
        withContext(Dispatchers.IO) {
            val direction = filteredTransaction.queryDirection ?: Query.Direction.ASCENDING
            val merchantId = pref.getMerchantId().first()
            val start = filteredTransaction.startDate ?: 0

            val dayMidnight = 86399000L
            val end = filteredTransaction.endDate?.plus(dayMidnight) ?: Utils.getTodayTimestamp()
                .plus(dayMidnight)
            val query = if (filteredTransaction.trxType != null) {
                db.collection("transaction")
                    .whereEqualTo("merchantId", merchantId)
                    .whereEqualTo("trxType", filteredTransaction.trxType)
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

            val listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let {
                    val data =
                        mutableListOf<Transaction>()

                    for (document in querySnapshot.documents) {
                        val amount = document.getLong("amount")
                        val type = document.getString("trxType")
                        val id = document.id
                        val timestamp = document.getLong("timestamp")

                        if (amount != null) {
                            data.add(
                                Transaction(
                                    amount, type, id, timestamp
                                )
                            )
                        }
                    }
                    callback(data)
                }
            }

            return@withContext listenerRegistration
        }

    fun getTotalAmountTransactions(listTransactions: List<Transaction>): Long =
        listTransactions.fold(0L) { totalAmount, transaction ->
            if (transaction.trxType == "PAYMENT") {
                totalAmount + transaction.amount
            } else {
                totalAmount - transaction.amount
            }
        }
}