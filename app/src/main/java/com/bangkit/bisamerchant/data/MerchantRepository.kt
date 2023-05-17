package com.bangkit.bisamerchant.data

import com.bangkit.bisamerchant.data.response.Merchant
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MerchantRepository(
    private val pref: MerchantPreferences
) {
    private val auth = Firebase.auth
    private val email = auth.currentUser?.email
    private val db = FirebaseFirestore.getInstance()

    suspend fun getMerchantActive(): QuerySnapshot = withContext(Dispatchers.IO) {
        return@withContext db.collection("merchant").whereEqualTo("merchantActive", true)
            .whereEqualTo("email", email).get().await()

    }

    fun processMerchantQuerySnapshot(querySnapshot: QuerySnapshot): Merchant {
        var data = Merchant()

        for (document in querySnapshot.documents) {
            saveMerchantId(document.id)
            val balance = document.getLong("balance")
            val merchantActive = document.getBoolean("merchantActive")
            val merchantLogo = document.getString("merchantLogo")
            val merchantAddress = document.getString("merchantAddress")
            val merchantType = document.getString("merchantType")
            val email = document.getString("email")
            val merchantName = document.getString("merchantName")

            data = Merchant(
                balance,
                merchantActive,
                merchantLogo,
                merchantAddress,
                merchantType,
                email,
                merchantName,
            )
        }

        return data
    }

    private fun saveMerchantId(id: String) {
        runBlocking {
            pref.saveMerchantId(id)
        }
    }

    companion object {
        @Volatile
        private var instance: MerchantRepository? = null
        fun getInstance(pref: MerchantPreferences): MerchantRepository =
            instance ?: synchronized(this) {
                instance ?: MerchantRepository(pref)
            }.also { instance = it }
    }
}