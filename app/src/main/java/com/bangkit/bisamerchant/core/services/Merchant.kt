package com.bangkit.bisamerchant.core.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.bangkit.bisamerchant.ui.home.HomeActivity
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception

object Merchant {
    private val storage = FirebaseStorage.getInstance()

    fun checkMerchantExists(onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        val email = Auth.getEmail()
        val merchantCollection = FirebaseFirestore.getInstance().collection("merchant")
        merchantCollection.whereEqualTo("email", email).get()
            .addOnSuccessListener { querySnapshot ->
                val exists = !querySnapshot.isEmpty
                onSuccess(exists)
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

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

    fun updateMerchant(
        activity: Activity,
        context: Context,
        photo: Uri?,
        name: String,
        address: String,
        type: String
    ) {
        val merchantCollection = FirebaseFirestore.getInstance().collection("merchant")
        val email = Auth.getEmail()
        val docMerchant =
            merchantCollection.whereEqualTo("email", email).whereEqualTo("merchantActive", true)

        if (photo != null) {
            docMerchant.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val merchantDoc = merchantCollection.document(document.id)
                    val storageRef = storage.reference
                    val docId = document.id
                    val imageRef = storageRef.child("merchant/logo/$docId.jpg")
                    imageRef.delete().addOnSuccessListener {
                        val uploadTask = imageRef.putFile(photo)
                        uploadTask.addOnSuccessListener { taskSnapshot ->
                            taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                                val data = mapOf(
                                    "merchantName" to name,
                                    "merchantLogo" to downloadUrl.toString(),
                                    "merchantAddress" to address,
                                    "merchantType" to type,
                                )
                                merchantDoc.update(data).addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Update merchant successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    activity.finish()
                                }.addOnFailureListener { error ->
                                    Toast.makeText(
                                        context,
                                        error.localizedMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }.addOnFailureListener { error ->
                                    Toast.makeText(
                                        context,
                                        error.localizedMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }.addOnFailureListener { error ->
                            Toast.makeText(
                                context, error.localizedMessage, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.addOnFailureListener { error ->
                        Toast.makeText(
                            context, error.localizedMessage, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.addOnFailureListener { error ->
                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        } else {
            docMerchant.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val docId = merchantCollection.document(document.id)
                    val data = mapOf(
                        "merchantName" to name,
                        "merchantAddress" to address,
                        "merchantType" to type,
                    )
                    docId.update(data).addOnSuccessListener {
                        Toast.makeText(
                            context, "Update merchant successful", Toast.LENGTH_SHORT
                        ).show()
                        activity.finish()
                    }.addOnFailureListener { error ->
                        Toast.makeText(
                            context, error.localizedMessage, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.addOnFailureListener { error ->
                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateBalanceMerchant(
        activity: Activity, context: Context, amount: Long, bankAccountNo: Long, bankInst: String
    ) {
        val merchantCollection = FirebaseFirestore.getInstance().collection("merchant")
        val email = Auth.getEmail()
        val docMerchant =
            merchantCollection.whereEqualTo("email", email).whereEqualTo("merchantActive", true)

        docMerchant.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                val docId = merchantCollection.document(document.id)
                docId.get().addOnSuccessListener { doc ->
                    if (doc != null) {
                        val data = document.data
                        val balanceMerchant = data["balance"].toString().toLong()
                        val newBalance = balanceMerchant - amount
                        docId.update("balance", newBalance).addOnSuccessListener {
                            Transaction.addTransaction(
                                activity,
                                context,
                                document.id,
                                amount,
                                bankAccountNo,
                                bankInst
                            )
                        }.addOnFailureListener { error ->
                            Toast.makeText(
                                context, error.localizedMessage, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }.addOnFailureListener { error ->
                    Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }.addOnFailureListener { error ->
            Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }
}