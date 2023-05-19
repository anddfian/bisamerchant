package com.bangkit.bisamerchant.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bangkit.bisamerchant.ui.home.HomeActivity
import com.bangkit.bisamerchant.ui.login.LoginActivity
import com.bangkit.bisamerchant.ui.register.MerchantRegisterActivity
import com.google.firebase.auth.FirebaseAuth

object Auth {
    private var auth = FirebaseAuth.getInstance()

    fun isLogged(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    fun login(activity: Activity, context: Context, email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Merchant.checkMerchantExists(
                        onSuccess = { exists ->
                            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                            if (exists) {
                                val intent = Intent(context, HomeActivity::class.java)
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
                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
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

    fun logout(activity: Activity, context: Context) {
        auth.signOut()
        Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        activity.finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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