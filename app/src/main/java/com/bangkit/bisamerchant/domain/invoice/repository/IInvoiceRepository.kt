package com.bangkit.bisamerchant.domain.invoice.repository

import com.bangkit.bisamerchant.domain.invoice.model.DetailTransaction
import kotlinx.coroutines.flow.Flow

interface IInvoiceRepository {
    suspend fun getTransaction(id: String): Flow<DetailTransaction>
}