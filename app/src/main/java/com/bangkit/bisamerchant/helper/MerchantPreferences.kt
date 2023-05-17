package com.bangkit.bisamerchant.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MerchantPreferences private constructor(private val dataStore: DataStore<Preferences>) {

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

    companion object {
        private val MERCHANT_ID = stringPreferencesKey("merchant_id")

        @Volatile
        private var INSTANCE: MerchantPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): MerchantPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = MerchantPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}