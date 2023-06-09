package com.bangkit.bisamerchant.data.setting.datasource

import com.bangkit.bisamerchant.data.utils.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingDataSource @Inject constructor(
    private val pref: SharedPreferences,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
) {
    suspend fun logout() = flow {
        try {
            pref.delete()
            auth.signOut()
            deleteTokenId()
            emit("You have been logged out successfully")
        } catch (e: Exception) {
            throw Exception(e.localizedMessage)
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun deleteTokenId() {
        try {
            val querySnapshot = db.collection("merchant")
                .whereEqualTo("email", auth.currentUser?.email)
                .get()
                .await()

            for (documentSnapshot in querySnapshot.documents) {
                val documentId = documentSnapshot.id
                try {
                    db.collection("merchant")
                        .document(documentId)
                        .update("tokenId", null)
                        .await()
                } catch (e: Exception) {
                    e.localizedMessage
                }
            }
        } catch (e: Exception) {
            e.localizedMessage
        }
    }
}
