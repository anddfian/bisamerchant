package com.bangkit.bisamerchant.services

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

object Transaction {
    fun addTransaction(activity: Activity, context: Context, merchantId: String, amount: Long, bankAccountNo: Long, bankInst: String) {
        val transactionCollection = FirebaseFirestore.getInstance().collection("transaction")
        val timestamp = System.currentTimeMillis()
        val data = hashMapOf(
            "amount" to amount,
            "bankAccountNo" to bankAccountNo,
            "bankInst" to bankInst,
            "merchantId" to merchantId,
            "trxType" to "MERCHANT_WITHDRAW",
            "timestamp" to timestamp
        )

        transactionCollection.add(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Withdraw successful", Toast.LENGTH_SHORT).show()
                    activity.finish()
                }
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

}