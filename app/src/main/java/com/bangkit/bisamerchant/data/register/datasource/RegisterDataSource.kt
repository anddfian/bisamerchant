package com.bangkit.bisamerchant.data.register.datasource

import com.bangkit.bisamerchant.core.helper.AESUtil
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
class RegisterDataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    suspend fun registerOwner(name: String, email: String, password: String, pin: String): Flow<String> = flow {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val encryptedPin = AESUtil.encrypt(pin)
            val data = hashMapOf(
                "name" to name,
                "email" to email,
                "pin" to encryptedPin
            )
            db.collection("owner").add(data).await()
            emit("Register successful")
        } catch (e: Exception) {
            emit(e.localizedMessage ?: "Failed to register")
        }
    }.flowOn(Dispatchers.IO)
}