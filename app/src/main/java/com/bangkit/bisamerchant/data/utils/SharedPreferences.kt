package com.bangkit.bisamerchant.data.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SharedPreferences constructor(context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data")
    private val dataStore = context.dataStore

    fun getMerchantId(): Flow<String> = dataStore.data.map { preferences ->
        preferences[MERCHANT_ID] ?: ""
    }

    suspend fun saveMerchantId(id: String) {
        dataStore.edit { preferences ->
            preferences[MERCHANT_ID] = id
        }
    }

    suspend fun delete() {
        dataStore.edit { preferences ->
            preferences[TOTAL_TRANSACTIONS] = 0
            preferences[MERCHANT_ID] = ""
            preferences[IS_AMOUNT_HIDE] = false
        }
    }

    fun getTransactionCount(): Flow<Long> = dataStore.data.map { preferences ->
        preferences[TOTAL_TRANSACTIONS] ?: 0
    }

    suspend fun updateTransactionCount(count: Long) {
        dataStore.edit { preferences ->
            preferences[TOTAL_TRANSACTIONS] = count
        }
    }

    fun getHideAmount(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_AMOUNT_HIDE] ?: false
    }

    suspend fun updateHideAmount(hide: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_AMOUNT_HIDE] = hide
        }
    }

    fun getNewUser(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NEW_USER] ?: false
    }

    suspend fun updateNewUser() {
        dataStore.edit { preferences ->
            preferences[NEW_USER] = true
        }
    }

    companion object {
        private val MERCHANT_ID = stringPreferencesKey("merchant_id")
        private val TOTAL_TRANSACTIONS = longPreferencesKey("total_transactions")
        private val NEW_USER = booleanPreferencesKey("new_user")
        private val IS_AMOUNT_HIDE = booleanPreferencesKey("is_amount_hide")

        @Volatile
        private var INSTANCE: SharedPreferences? = null

        fun getInstance(context: Context): SharedPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SharedPreferences(context)
                INSTANCE = instance
                instance
            }
        }
    }
}