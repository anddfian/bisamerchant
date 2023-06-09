package com.bangkit.bisamerchant.data.setting.datasource

import com.bangkit.bisamerchant.data.utils.SharedPreferences
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class SettingDataSource @Inject constructor(
    private val pref: SharedPreferences,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
) {
    suspend fun logout() = flow {
        try {
            deleteTokenId()
            auth.signOut()
            pref.delete()
            emit("You have been logged out successfully")
        } catch (e: Exception) {
            throw Exception(e.localizedMessage)
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun deleteTokenId() {
        return suspendCoroutine { continuation ->
            try {
                db.collection("merchant")
                    .whereEqualTo("email", auth.currentUser?.email)
                    .limit(7)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val documents = snapshot.documents

                        val updateTasks = documents.map { document ->
                            db.collection("merchant")
                                .document(document.id)
                                .update("tokenId", null)
                        }

                        // Wait for all update tasks to complete
                        Tasks.whenAllComplete(updateTasks)
                            .addOnSuccessListener {
                                continuation.resume(Unit)
                            }
                            .addOnFailureListener { exception ->
                                continuation.resumeWithException(exception)
                            }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }
}
