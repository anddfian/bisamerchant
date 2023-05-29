package com.bangkit.bisamerchant.core.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
        dataStore.edit { it.clear() }
    }

    fun getTransactionCount(): Flow<Int> = dataStore.data.map { preferences ->
        preferences[TOTAL_TRANSACTIONS] ?: 0
    }

    suspend fun saveTransactionCount(count: Int) {
        dataStore.edit { preferences ->
            preferences[TOTAL_TRANSACTIONS] = count
        }
    }

    fun getAmountHide(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_AMOUNT_HIDE] ?: false
    }

    suspend fun saveAmountHide(hide: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_AMOUNT_HIDE] = hide
        }
    }

    companion object {
        private val MERCHANT_ID = stringPreferencesKey("merchant_id")
        private val TOTAL_TRANSACTIONS = intPreferencesKey("total_transactions")
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