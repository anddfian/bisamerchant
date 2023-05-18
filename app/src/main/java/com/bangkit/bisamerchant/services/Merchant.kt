package com.bangkit.bisamerchant.services

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

    fun addMerchant(context: Context, photo: Uri, name: String, location: String, type: String) {
        val merchantCollection = FirebaseFirestore.getInstance().collection("merchant")
        val email = Auth.getEmail()

        val ref = storage.reference
        val imageRef = ref.child("images/$name.jpg")

        val uploadTask = imageRef.putFile(photo)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                val data = hashMapOf(
                    "email" to email,
                    "merchantLogo" to downloadUrl.toString(),
                    "merchantName" to name,
                    "merchantAddress" to location,
                    "merchantType" to type,
                    "merchantActive" to "Active",
                    "balance" to 0
                )

                merchantCollection.add(data)
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
}