package com.bangkit.bisamerchant.data.history.datasource

import com.bangkit.bisamerchant.data.utils.SharedPreferences
import com.bangkit.bisamerchant.data.utils.Utils
import com.bangkit.bisamerchant.domain.history.model.FilteredTransaction
import com.bangkit.bisamerchant.domain.history.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryDataSource @Inject constructor(
    private val pref: SharedPreferences,
    private val db: FirebaseFirestore,
) {

    suspend fun getTransactions() = flow {
        try {
            val merchantId = pref.getMerchantId().first()
            val data = mutableListOf<Transaction>()
            db.collection("transaction").whereEqualTo("merchantId", merchantId)
                .orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener {
                    for (document in it.documents) {
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
                }.await()
            emit(data)
        } catch (e: Exception) {
            throw Exception(e.localizedMessage)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getTransactionsWithFilter(
        filteredTransaction: FilteredTransaction
    ) = flow {
        try {
            val data = mutableListOf<Transaction>()
            val direction = filteredTransaction.queryDirection ?: Query.Direction.ASCENDING
            val merchantId = pref.getMerchantId().first()
            val start = filteredTransaction.startDate ?: 0

            val dayMidnight = 86399000L
            val end = filteredTransaction.endDate?.plus(dayMidnight) ?: Utils.getTodayTimestamp()
                .plus(dayMidnight)

            val query = if (filteredTransaction.trxType != null) {
                db.collection("transaction").whereEqualTo("merchantId", merchantId)
                    .whereEqualTo("trxType", filteredTransaction.trxType)
                    .whereGreaterThanOrEqualTo("timestamp", start)
                    .whereLessThanOrEqualTo("timestamp", end).orderBy("timestamp", direction)
            } else {
                db.collection("transaction").whereEqualTo("merchantId", merchantId)
                    .whereGreaterThanOrEqualTo("timestamp", start)
                    .whereLessThanOrEqualTo("timestamp", end).orderBy("timestamp", direction)
            }

            query.get().addOnSuccessListener {
                for (document in it.documents) {
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
            }.await()
            emit(data)
        } catch (e: Exception) {
            throw Exception(e.localizedMessage)
        }
    }.flowOn(Dispatchers.IO)
}