package com.bangkit.bisamerchant.data.splash.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SplashDatasource @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    suspend fun getMerchantActive(): Flow<String> = flow {
        try {
            val currentUser = auth.currentUser ?: throw Exception(OWNER_NOT_AUTHENTICATED)
            val querySnapshot = db.collection("merchant")
                .whereEqualTo("merchantActive", true)
                .whereEqualTo("email", currentUser.email)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                emit(MERCHANT_NOT_FOUND)
            } else if (!querySnapshot.isEmpty){
                emit(OWNER_AUTHENTICATED)
            }
        } catch (e: Exception) {
            throw Exception(e.localizedMessage)
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        private const val MERCHANT_NOT_FOUND = "Merchant not found"
        private const val OWNER_NOT_AUTHENTICATED = "Owner is not logged in"
        private const val OWNER_AUTHENTICATED = "Logged in"
    }
}