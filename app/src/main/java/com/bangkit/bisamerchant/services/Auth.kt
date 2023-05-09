package com.bangkit.bisamerchant.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bangkit.bisamerchant.ui.home.HomeActivity
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
                    val intent = Intent(context, HomeActivity::class.java)
                    context.startActivity(intent)
                    activity.finish()
                }
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }
}