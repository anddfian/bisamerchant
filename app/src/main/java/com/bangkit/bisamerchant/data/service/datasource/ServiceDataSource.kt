package com.bangkit.bisamerchant.data.service.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
) {
    suspend fun postTokenId(token: String) {
        try {
            val querySnapshot = db.collection("merchant")
                .whereEqualTo("email", auth.currentUser?.email)
                .get()
                .await()

            for (documentSnapshot in querySnapshot.documents) {
                val documentId = documentSnapshot.id
                try {
                    db.collection("merchant").document(documentId).update("tokenId", token)
                } catch (e: Exception) {
                    e.localizedMessage
                }
            }
        } catch (e: Exception) {
            e.localizedMessage
        }
    }
}