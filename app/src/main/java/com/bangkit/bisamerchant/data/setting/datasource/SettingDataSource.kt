package com.bangkit.bisamerchant.data.setting.datasource

import com.bangkit.bisamerchant.data.utils.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingDataSource @Inject constructor(
    private val pref: SharedPreferences,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
) {
    suspend fun logout() {
        withContext(Dispatchers.IO) {
            deleteUserDevice()
            pref.delete()
        }
        auth.signOut()
    }

    private suspend fun deleteUserDevice() {
        try {
            val querySnapshot = db.collection("owner")
                .whereEqualTo("email", auth.currentUser?.email)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents[0]
                val documentId = documentSnapshot.id

                try {
                    db.collection("owner").document(documentId).update("deviceToken", null)
                } catch (e: Exception) {
                    e.localizedMessage
                }
            }
        } catch (e: Exception) {
            e.localizedMessage
        }
    }
}
