package com.bangkit.bisamerchant.domain.invoice.usecase

import com.bangkit.bisamerchant.domain.invoice.repository.IInvoiceRepository
import javax.inject.Inject

class GetTransaction @Inject constructor(private val invoiceRepository: IInvoiceRepository) {

    suspend fun execute(id: String) =
        invoiceRepository.getTransaction(id)
}