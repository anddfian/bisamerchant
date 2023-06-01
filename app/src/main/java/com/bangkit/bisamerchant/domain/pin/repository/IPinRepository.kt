package com.bangkit.bisamerchant.domain.pin.repository

import kotlinx.coroutines.flow.Flow

interface IPinRepository {
    suspend fun validateOwnerPin(inputPin: Int): Flow<Boolean>
}