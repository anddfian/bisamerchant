package com.bangkit.bisamerchant.data.setting.datasource

import com.bangkit.bisamerchant.data.utils.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingDataSource @Inject constructor(
    private val pref: SharedPreferences,
) {
    suspend fun deleteMerchant() {
        withContext(Dispatchers.IO) {
            pref.delete()
        }
    }
}