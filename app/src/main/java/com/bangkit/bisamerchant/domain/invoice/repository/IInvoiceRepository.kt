package com.bangkit.bisamerchant.domain.invoice.repository

import com.bangkit.bisamerchant.domain.invoice.model.DetailTransaction

interface IInvoiceRepository {
    suspend fun getTransaction(id: String): DetailTransaction
}