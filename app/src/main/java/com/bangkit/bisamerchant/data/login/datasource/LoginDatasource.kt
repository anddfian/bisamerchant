package com.bangkit.bisamerchant.data.login.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginDatasource @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
) {
    fun login(email: String, password: String): Flow<String> = flow {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            val querySnapshot = db.collection("merchant")
                .whereEqualTo("email", email)
                .whereEqualTo("merchantActive", true)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                emit("Login successful")
            } else {
                emit("Merchant not found")
            }
        } catch (e: Exception) {
            emit(e.localizedMessage ?: "Failed to login")
        }
    }.flowOn(Dispatchers.IO)
}