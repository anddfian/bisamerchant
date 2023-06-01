package com.bangkit.bisamerchant.core.services

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bangkit.bisamerchant.core.helper.AESUtil
import com.bangkit.bisamerchant.presentation.register.MerchantRegisterActivity
import com.google.firebase.firestore.FirebaseFirestore

object Owner {

    fun addOwner(context: Context, name: String, email: String, pin: String) {
        val ownerCollection = FirebaseFirestore.getInstance().collection("owner")
        val encryptedPin = AESUtil.encrypt(pin)
        val data = hashMapOf(
            "name" to name,
            "email" to email,
            "pin" to encryptedPin
        )

        ownerCollection.add(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Register successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MerchantRegisterActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }
}