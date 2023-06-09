package com.bangkit.bisamerchant.data.invoice.datasource

import com.bangkit.bisamerchant.domain.invoice.model.DetailTransaction
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvoiceDataSource @Inject constructor(
    private val db: FirebaseFirestore,
) {
    suspend fun getTransaction(id: String) = flow {
        try {
            val query = db.collection("transaction").document(id).get().await()

            val documentId = query.id
            val amount = query.getLong("amount")
            val merchantId = query.getString("merchantId")
            val trxType = query.getString("trxType")
            val timestamp = query.getLong("timestamp")

            if (trxType == "PAYMENT") {
                val payerId = query.getString("payerId")
                emit(
                    DetailTransaction(
                        id = documentId,
                        amount = amount,
                        merchantId = merchantId,
                        payerId = payerId,
                        trxType = trxType,
                        timestamp = timestamp
                    )
                )
            } else {
                val bankAccountNo = query.getLong("bankAccountNo")
                val bankInst = query.getString("bankInst")
                emit(
                    DetailTransaction(
                        id = id,
                        amount = amount,
                        merchantId = merchantId,
                        bankAccountNo = bankAccountNo,
                        bankInst = bankInst,
                        trxType = trxType,
                        timestamp = timestamp
                    )
                )
            }
        } catch (e: Exception) {
            throw Exception(e.localizedMessage)
        }
    }.flowOn(Dispatchers.IO)
}