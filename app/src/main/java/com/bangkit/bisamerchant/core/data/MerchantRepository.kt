package com.bangkit.bisamerchant.core.data

import com.bangkit.bisamerchant.core.domain.model.Merchant
import com.bangkit.bisamerchant.core.domain.repository.IMerchantRepository
import com.bangkit.bisamerchant.core.helper.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MerchantRepository @Inject constructor(
    private val pref: SharedPreferences,
    private val db: FirebaseFirestore,
    auth: FirebaseAuth,
) : IMerchantRepository {
    private val email = auth.currentUser?.email
    private var listenerRegistration: ListenerRegistration? = null

    override suspend fun observeMerchantActive(callback: (Merchant) -> Unit): ListenerRegistration {
        return withContext(Dispatchers.IO) {
            val query = db.collection("merchant").whereEqualTo("merchantActive", true)
                .whereEqualTo("email", email)

            listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let {
                    val merchant = processMerchantActiveQuerySnapshot(it)
                    callback(merchant)
                }
            }

            return@withContext listenerRegistration as ListenerRegistration
        }
    }

    override suspend fun observeMerchants(callback: (List<Merchant>) -> Unit): ListenerRegistration {
        return withContext(Dispatchers.IO) {
            val query = db.collection("merchant").whereEqualTo("email", email)

            listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let {
                    val merchants = processMerchantsQuerySnapshot(it)
                    callback(merchants)
                }
            }

            return@withContext listenerRegistration as ListenerRegistration
        }
    }


    override fun stopObserving() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }

    override fun processMerchantActiveQuerySnapshot(querySnapshot: QuerySnapshot): Merchant {
        var data = Merchant()

        for (document in querySnapshot.documents) {
            runBlocking {
                saveMerchantId(document.id)
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

        return data
    }

    override fun processMerchantsQuerySnapshot(querySnapshot: QuerySnapshot): List<Merchant> {
        val data = mutableListOf<Merchant>()

        for (document in querySnapshot.documents) {
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

    override suspend fun changeMerchantStatus(id: String?) {
        withContext(Dispatchers.IO) {
            val merchantActiveNow = getMerchantId()
            deleteMerchant()
            if (id != merchantActiveNow) {
                id?.let { it1 -> saveMerchantId(it1) }
                val merchantCollection = FirebaseFirestore.getInstance().collection("merchant")
                merchantCollection.whereEqualTo("email", email).get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            querySnapshot.forEach {
                                val isActive = it.getBoolean("merchantActive")
                                if (isActive == false && it.id == id) {
                                    merchantCollection.document(it.id)
                                        .update("merchantActive", it.id == id)
                                }
                            }
                            merchantCollection.document(merchantActiveNow)
                                .update("merchantActive", false)
                        }
                    }
            }

        }
    }

    override suspend fun getMerchantId() =
        withContext(Dispatchers.IO) {
            pref.getMerchantId().first()
        }


    override suspend fun saveMerchantId(id: String) {
        withContext(Dispatchers.IO) {
            pref.saveMerchantId(id)
        }
    }

    override suspend fun saveAmountHide(hide: Boolean) {
        withContext(Dispatchers.IO) {
            pref.saveAmountHide(hide)
        }
    }

    override suspend fun getAmountHide() =
        withContext(Dispatchers.IO) {
            pref.getAmountHide().first()
        }

    override suspend fun deleteMerchant() {
        withContext(Dispatchers.IO) {
            pref.delete()
        }
    }
}