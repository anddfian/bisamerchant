package com.bangkit.bisamerchant.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.bangkit.bisamerchant.ui.home.HomeActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception

object Merchant {
    private val storage = FirebaseStorage.getInstance()

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

    fun addMerchant(context: Context, photo: Uri, name: String, address: String, type: String) {
        val merchantCollection = FirebaseFirestore.getInstance().collection("merchant")
        val email = Auth.getEmail()

        val data = hashMapOf(
            "email" to email,
            "merchantName" to name,
            "merchantAddress" to address,
            "merchantType" to type,
            "merchantActive" to true,
            "balance" to 0,
            "transactionCount" to 0
        )

        merchantCollection.add(data)
            .addOnSuccessListener { documentReference ->
                val docId = documentReference.id

                val storageRef = storage.reference
                val imageRef = storageRef.child("merchant/logo/$docId.jpg")

                val uploadTask = imageRef.putFile(photo)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                        merchantCollection.document(docId).update("merchantLogo", downloadUrl)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Register merchant successful", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context, HomeActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                }
                            }
                            .addOnFailureListener { error ->
                                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
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

    fun updateMerchant(activity: Activity, context: Context, photo: Uri?, name: String, address: String, type: String) {
        val merchantCollection = FirebaseFirestore.getInstance().collection("merchant")
        val email = Auth.getEmail()
        val docMerchant =
            merchantCollection.whereEqualTo("email", email).whereEqualTo("merchantActive", true)

        if (photo != null) {
            docMerchant.get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val docId = merchantCollection.document(document.id)
                        val storageRef = storage.reference
                        var imageRef = storageRef.child("merchant/logo/$docId.jpg")
                        imageRef.delete()
                            .addOnSuccessListener {
                                imageRef = storageRef.child("merchant/logo/$docId.jpg")

                                val uploadTask = imageRef.putFile(photo)
                                uploadTask
                                    .addOnSuccessListener { taskSnapshot ->
                                        taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                                            val data = mapOf(
                                                "merchantName" to name,
                                                "merchantLogo" to downloadUrl.toString(),
                                                "merchantAddress" to address,
                                                "merchantType" to type,
                                            )
                                            docId.update(data)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "Update merchant successful", Toast.LENGTH_SHORT).show()
                                                    activity.finish()
                                                }
                                                .addOnFailureListener { error ->
                                                    Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
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
                .addOnFailureListener { error ->
                    Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        } else {
            docMerchant.get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val docId = merchantCollection.document(document.id)
                        val data = mapOf(
                            "merchantName" to name,
                            "merchantAddress" to address,
                            "merchantType" to type,
                        )
                        docId.update(data)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Update merchant successful", Toast.LENGTH_SHORT).show()
                                activity.finish()
                            }
                            .addOnFailureListener { error ->
                                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { error ->
                    Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        }
    }
}