package com.bangkit.bisamerchant.data.pin.repository

import com.bangkit.bisamerchant.data.pin.datasource.PinDataSource
import com.bangkit.bisamerchant.domain.pin.repository.IPinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinRepository @Inject constructor(
    private val pinDataSource: PinDataSource
) : IPinRepository {
    override suspend fun validateOwnerPin(inputPin: Int): Flow<Boolean> = flow {
        val pin = pinDataSource.getOwnerPin()
        if (pin.toInt() == inputPin) {
            emit(true)
        } else {
            emit(false)
        }
    }
}