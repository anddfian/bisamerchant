package com.bangkit.bisamerchant.core.services

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

object Auth {
    private var auth = FirebaseAuth.getInstance()

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

    fun getEmail(): String? {
        return auth.currentUser?.email
    }
}
