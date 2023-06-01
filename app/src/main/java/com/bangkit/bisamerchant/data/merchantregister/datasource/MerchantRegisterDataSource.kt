package com.bangkit.bisamerchant.data.merchantregister.datasource

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MerchantRegisterDataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
) {

    private fun changeMerchantStatus(id: String) {
        val merchantCollection = db.collection("merchant")

        merchantCollection.whereEqualTo("email", auth.currentUser?.email)
            .whereEqualTo("merchantActive", true).whereNotEqualTo(
                FieldPath.documentId(), id
            ).get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    querySnapshot.forEach {
                        merchantCollection.document(it.id).update("merchantActive", false)
                    }
                }
            }
    }

    fun addMerchant(name: String, address: String, type: String, photo: Uri) = flow {
        val merchantCollection = db.collection("merchant")
        val newMerchantId = merchantCollection.document().id

        val data = hashMapOf(
            "id" to newMerchantId,
            "email" to auth.currentUser?.email,
            "merchantName" to name,
            "merchantAddress" to address,
            "merchantType" to type,
            "merchantActive" to true,
            "balance" to 0,
            "transactionCount" to 0
        )

        try {
            merchantCollection.document(newMerchantId).set(data).await()
            changeMerchantStatus(newMerchantId)

            val storageRef = storage.reference
            val imageRef = storageRef.child("merchant/logo/$newMerchantId.jpg")

            val uploadTask = imageRef.putFile(photo).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()

            merchantCollection.document(newMerchantId)
                .update("merchantLogo", downloadUrl.toString())

            emit("Register merchant successful")
        } catch (e: Exception) {
            emit(e.localizedMessage ?: "Failed to register merchant")
        }
    }.flowOn(Dispatchers.IO)

}