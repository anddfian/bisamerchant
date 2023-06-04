package com.bangkit.bisamerchant.domain.service.repository

interface IServiceRepository {
    suspend fun postRegistrationToken(token: String)
}