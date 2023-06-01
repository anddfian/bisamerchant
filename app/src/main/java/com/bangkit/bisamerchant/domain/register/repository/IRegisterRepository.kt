package com.bangkit.bisamerchant.domain.register.repository

import kotlinx.coroutines.flow.Flow

interface IRegisterRepository {
    suspend fun registerOwner(name: String, email: String, password: String, pin: String): Flow<String>
}