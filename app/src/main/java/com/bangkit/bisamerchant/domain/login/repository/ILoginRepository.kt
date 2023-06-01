package com.bangkit.bisamerchant.domain.login.repository

import kotlinx.coroutines.flow.Flow

interface ILoginRepository {
    suspend fun login(email: String, password: String): Flow<String>
}