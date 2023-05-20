package com.bangkit.bisamerchant.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bangkit.bisamerchant.data.response.Merchant
import com.bangkit.bisamerchant.helper.MerchantPreferences
import com.bangkit.bisamerchant.services.Auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MerchantRepository(
    private val pref: MerchantPreferences
) {
    private val auth = Firebase.auth
    private val email = auth.currentUser?.email
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    fun observeMerchantActive(callback: (Merchant) -> Unit): ListenerRegistration {
        val query = db.collection("merchant").whereEqualTo("merchantActive", true)
            .whereEqualTo("email", email)

        listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
            querySnapshot?.let {
                val merchant = processMerchantActiveQuerySnapshot(it)
                callback(merchant)
            }
        }

        return listenerRegistration as ListenerRegistration
    }

    fun observeMerchants(callback: (List<Merchant>) -> Unit): ListenerRegistration {
        val query = db.collection("merchant").whereEqualTo("email", email)

        listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
            querySnapshot?.let {
                val merchants = processMerchantsQuerySnapshot(it)
                callback(merchants)
            }
        }

        return listenerRegistration as ListenerRegistration
    }


    fun stopObserving() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }

    private fun processMerchantActiveQuerySnapshot(querySnapshot: QuerySnapshot): Merchant {
        var data = Merchant()

        for (document in querySnapshot.documents) {
            saveMerchantId(document.id)
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

        return data
    }

    private fun processMerchantsQuerySnapshot(querySnapshot: QuerySnapshot): List<Merchant> {
        var data = mutableListOf<Merchant>()

        for (document in querySnapshot.documents) {
            saveMerchantId(document.id)
            val id = document.id
            val balance = document.getLong("balance")
            val merchantActive = document.getBoolean("merchantActive")
            val merchantLogo = document.getString("merchantLogo")
            val merchantAddress = document.getString("merchantAddress")
            val merchantType = document.getString("merchantType")
            val email = document.getString("email")
            val merchantName = document.getString("merchantName")
            val transactionCount = document.getLong("transactionCount")

            data.add(
                Merchant(
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
            )
        }

        return data
    }

    fun changeMerchantStatus(id: String?) {
        val merchantCollection = FirebaseFirestore.getInstance().collection("merchant")
        var merchantNow = ""
        deleteMerchant()
        id?.let { it1 -> saveMerchantId(it1) }
        merchantCollection.whereEqualTo("email", email).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    querySnapshot.forEach {
                        val isActive = it.getBoolean("merchantActive")
                        if (isActive == false && it.id == id) {
                            merchantCollection.document(it.id).update("merchantActive", it.id == id)
                        }
                        if (isActive == true && it.id != id) {
                            merchantNow = it.id
                        }
                    }
                    merchantCollection.document(merchantNow).update("merchantActive", false)
                }
            }
    }

    fun saveMerchantId(id: String) {
        runBlocking {
            pref.saveMerchantId(id)
        }
    }

    fun deleteMerchant() {
        runBlocking {
            pref.delete()
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