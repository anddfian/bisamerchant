package com.bangkit.bisamerchant.data.pin.datasource

import com.bangkit.bisamerchant.data.utils.AESUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinDataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    suspend fun getOwnerPin(): String =
        withContext(Dispatchers.IO) {
            try {
                val querySnapshot = db.collection("owner")
                    .whereEqualTo("email", auth.currentUser?.email)
                    .get()
                    .await()

                val document = querySnapshot.documents.firstOrNull()
                val pin = document?.getString("pin")
                val decryptedPin = AESUtil.decrypt(pin.toString())
                decryptedPin
            } catch (e: Exception) {
                e.localizedMessage ?: "Wrong PIN"
            }
        }

}