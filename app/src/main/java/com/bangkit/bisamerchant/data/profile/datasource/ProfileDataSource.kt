package com.bangkit.bisamerchant.data.profile.datasource

import com.bangkit.bisamerchant.data.utils.SharedPreferences
import com.bangkit.bisamerchant.domain.profile.model.Merchant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileDataSource @Inject constructor(
    private val pref: SharedPreferences,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    suspend fun getMerchantActive(callback: (Merchant) -> Unit): ListenerRegistration {
        return withContext(Dispatchers.IO) {
            val query = db.collection("merchant").whereEqualTo("merchantActive", true)
                .whereEqualTo("email", auth.currentUser?.email)

            val listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let {
                    var data = Merchant()

                    for (document in querySnapshot.documents) {
                        runBlocking {
                            pref.saveMerchantId(document.id)
                        }
                        val id = document.id
                        val balance = document.getLong("balance")
                        val merchantActive = document.getBoolean("merchantActive")
                        val merchantLogo = document.getString("merchantLogo")
                        val merchantAddress = document.getString("merchantAddress")
                        val merchantType = document.getString("merchantType")
                        val email = document.getString("email")
                        val merchantName = document.getString("merchantName")
                        val transactionCount = document.getLong("transactionCount")

                        data = Merchant(
                            id,
                            balance,
                            merchantActive,
                            merchantLogo,
                            merchantAddress,
                            merchantType,
                            email,
                            merchantName,
                            transactionCount
                        )
                    }

                    callback(data)
                }
            }

            return@withContext listenerRegistration
        }
    }
}