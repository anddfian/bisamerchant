package com.bangkit.bisamerchant.core.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bangkit.bisamerchant.presentation.register.MerchantRegisterActivity
import com.bangkit.bisamerchant.presentation.splashscreen.SplashScreenActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

object Auth {
    private var auth = FirebaseAuth.getInstance()

    fun isLogged(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    private fun checkOwnerExists(
        email: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
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

    fun login(activity: Activity, context: Context, email: String, password: String) {
        checkOwnerExists(email,
            onSuccess = { exists ->
                if (exists) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Merchant.checkMerchantExists(
                                    onSuccess = { exists ->
                                        Toast.makeText(
                                            context,
                                            "Login successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        if (exists) {
                                            // Arahkan ke SplashScreen dulu buat retrieve data
                                            val intent =
                                                Intent(context, SplashScreenActivity::class.java)
                                            context.startActivity(intent)
                                            activity.finish()
                                        } else {
                                            val intent = Intent(
                                                context,
                                                MerchantRegisterActivity::class.java
                                            )
                                            context.startActivity(intent)
                                            activity.finish()
                                        }
                                    },
                                    onFailure = { exception ->
                                        Toast.makeText(
                                            context,
                                            exception.localizedMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                } else {
                    Toast.makeText(context, "Account not exists!", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { exception ->
                Toast.makeText(
                    context,
                    exception.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    fun resetPasswordEmail(context: Context, email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Password reset email sent", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    fun register(context: Context, name: String, email: String, password: String, pin: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Owner.addOwner(context, name, email, pin)
                }
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    fun getEmail(): String? {
        return auth.currentUser?.email
    }
}
