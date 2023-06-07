package com.bangkit.bisamerchant.data.login.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginDatasource @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val messaging: FirebaseMessaging,
) {
    fun login(email: String, password: String): Flow<String> = flow {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            val querySnapshot = db.collection("merchant").whereEqualTo("email", email)
                .whereEqualTo("merchantActive", true).limit(1).get().await()

            if (!querySnapshot.isEmpty) {
                auth.currentUser?.email?.let { postRegistrationToken(it) }
                emit(LOGIN_SUCCESSFUL)
            } else {
                throw Exception(MERCHANT_NOT_FOUND)
            }
        } catch (e: Exception) {
            throw Exception(e.localizedMessage ?: "Failed to login")
        }
    }

    suspend fun postRegistrationToken(email: String) {
        try {
            val token = messaging.token.await()
            val querySnapshot = db.collection("merchant")
                .whereEqualTo("email", email)
                .get()
                .await()

            for (documentSnapshot in querySnapshot.documents) {
                val documentId = documentSnapshot.id
                try {
                    db.collection("merchant").document(documentId).update("tokenId", token).await()
                } catch (e: Exception) {
                    e.localizedMessage
                }
            }
        } catch (e: Exception) {
            e.localizedMessage
        }
    }

    fun resetPassword(email: String): Flow<String> = flow {
        try {
            auth.sendPasswordResetEmail(email).await()
            emit(RESET_SUCCESSFUL)
        } catch (e: Exception) {
            throw Exception(e.localizedMessage)
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        private const val MERCHANT_NOT_FOUND = "Merchant not found"
        private const val LOGIN_SUCCESSFUL = "Login successful"
        private const val RESET_SUCCESSFUL = "Reset password sent, check your email"
    }
}