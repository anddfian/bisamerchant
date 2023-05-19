package com.bangkit.bisamerchant.services

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bangkit.bisamerchant.ui.register.MerchantRegisterActivity
import com.bangkit.bisamerchant.helper.AESUtil
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

object Owner {

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPinOwner(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val email = Auth.getEmail()
        val ownerCollection = FirebaseFirestore.getInstance().collection("owner")
        ownerCollection
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val ownerDoc = ownerCollection.document(document.id)
                    ownerDoc.get()
                        .addOnSuccessListener { doc ->
                            if (doc != null) {
                                val data = document.data
                                val pin = data["pin"]
                                val decryptedPin = AESUtil.decrypt(pin.toString())
                                onSuccess(decryptedPin)
                            }
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}