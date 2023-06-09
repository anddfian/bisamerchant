package com.bangkit.bisamerchant.data.merchantsetting.datasource

import android.net.Uri
import com.bangkit.bisamerchant.data.utils.SharedPreferences
import com.bangkit.bisamerchant.domain.merchantsetting.model.Merchant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MerchantSettingDataSource @Inject constructor(
    private val pref: SharedPreferences,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
) {
    suspend fun getMerchantActive() = flow {
        try {
            val merchantId = pref.getMerchantId().first()
            val merchant = db.collection("merchant").document(merchantId).get().await()

            merchant.apply {
                emit(
                    Merchant(
                        getString("merchantLogo"),
                        getString("merchantAddress"),
                        getString("merchantType"),
                        getString("merchantName"),
                    )
                )
            }
        } catch (e: Exception) {
            throw Exception(e.localizedMessage)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updateMerchantActive(name: String, address: String, type: String, newPhoto: Uri?) = flow {
        val merchantId = pref.getMerchantId().first()
        val merchantDocument = db.collection("merchant").document(merchantId)

        if (newPhoto != null) {
            val storageRef = storage.reference
            val imageRef = storageRef.child("merchant/logo/$merchantId.jpg")

            try {
                imageRef.delete().await()

                val uploadTask = imageRef.putFile(newPhoto).await()
                val downloadUrl = uploadTask.storage.downloadUrl.await()

                val data = mapOf(
                    "merchantName" to name,
                    "merchantLogo" to downloadUrl.toString(),
                    "merchantAddress" to address,
                    "merchantType" to type
                )

                merchantDocument.update(data).await()
                emit("Update merchant successful")
            } catch (e: Exception) {
                emit(e.localizedMessage ?: "Failed to update merchant")
            }
        } else {
            try {
                val data = mapOf(
                    "merchantName" to name,
                    "merchantAddress" to address,
                    "merchantType" to type
                )
                merchantDocument.update(data).await()
                emit("Update merchant successful")
            } catch (e: Exception) {
                emit(e.localizedMessage ?: "Failed to update merchant")
            }
        }
    }
}