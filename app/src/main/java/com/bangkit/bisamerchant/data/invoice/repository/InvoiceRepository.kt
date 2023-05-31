package com.bangkit.bisamerchant.data.invoice.repository

import com.bangkit.bisamerchant.data.invoice.datasource.InvoiceDataSource
import com.bangkit.bisamerchant.domain.invoice.repository.IInvoiceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvoiceRepository @Inject constructor(
    private val invoiceDataSource: InvoiceDataSource
): IInvoiceRepository {
    override suspend fun getTransaction(id: String) =
        invoiceDataSource.getTransaction(id)

}