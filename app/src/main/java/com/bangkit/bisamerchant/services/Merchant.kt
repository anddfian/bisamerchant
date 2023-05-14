package com.bangkit.bisamerchant.services

import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

object Merchant {

    fun checkMerchantExists(onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        val email = Auth.getEmail()
        val merchantCollection = FirebaseFirestore.getInstance().collection("merchant")
        merchantCollection
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val exists = !querySnapshot.isEmpty
                onSuccess(exists)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}