package com.bangkit.bisamerchant.domain.setting.repository

import kotlinx.coroutines.flow.Flow

interface ISettingRepository {
    suspend fun logout(): Flow<String>
}
