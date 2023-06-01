package com.bangkit.bisamerchant.core.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.bangkit.bisamerchant.presentation.home.activity.HomeActivity
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object Merchant {
    private val storage = FirebaseStorage.getInstance()

    private fun changeMerchantStatus(id: String) {
        val email = Auth.getEmail()
        val merchantCollection = FirebaseFirestore.getInstance().collection("merchant")

        merchantCollection.whereEqualTo("email", email).whereEqualTo("merchantActive", true)
            .whereNotEqualTo(
                FieldPath.documentId(), id
            ).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    querySnapshot.forEach {
                        merchantCollection.document(it.id).update("merchantActive", false)
                    }
                }
            }
    }

    fun addMerchant(context: Context, photo: Uri, name: String, address: String, type: String) {
        val merchantCollection = FirebaseFirestore.getInstance().collection("merchant")
        val email = Auth.getEmail()
        val newMerchantId = merchantCollection.document().id

        val data = hashMapOf(
            "id" to newMerchantId,
            "email" to email,
            "merchantName" to name,
            "merchantAddress" to address,
            "merchantType" to type,
            "merchantActive" to true,
            "balance" to 0,
            "transactionCount" to 0
        )

        merchantCollection.document(newMerchantId)
            .set(data)
            .addOnSuccessListener {
                changeMerchantStatus(newMerchantId)

                val storageRef = storage.reference
                val imageRef = storageRef.child("merchant/logo/$newMerchantId.jpg")

                val uploadTask = imageRef.putFile(photo)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                        merchantCollection.document(newMerchantId)
                            .update("merchantLogo", downloadUrl)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Register merchant successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(context, HomeActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                }
                            }
                            .addOnFailureListener { error ->
                                Toast.makeText(
                                    context, error.localizedMessage, Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
                    .addOnFailureListener { error ->
                        Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }
}