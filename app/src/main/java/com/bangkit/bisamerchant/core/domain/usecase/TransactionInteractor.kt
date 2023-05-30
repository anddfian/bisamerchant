package com.bangkit.bisamerchant.core.domain.usecase

import com.bangkit.bisamerchant.core.domain.model.Payment
import com.bangkit.bisamerchant.core.domain.model.Transaction
import com.bangkit.bisamerchant.core.domain.repository.ITransactionRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class TransactionInteractor @Inject constructor(private val transactionRepository: ITransactionRepository) :
    TransactionUseCase {
    override suspend fun addTransaction(payment: Payment) =
        transactionRepository.addTransaction(payment)


    override suspend fun getPayerBalance(payerId: String) =
        transactionRepository.getPayerBalance(payerId)


    override suspend fun observeTransactionsToday(callback: (List<Transaction>) -> Unit) =
        transactionRepository.observeTransactionsToday(callback)


    override fun processTransactionQuerySnapshot(querySnapshot: QuerySnapshot) =
        transactionRepository.processTransactionQuerySnapshot(querySnapshot)


    override fun observeTransactions(callback: (List<Transaction>) -> Unit) =
        transactionRepository.observeTransactions(callback)

    override suspend fun observeTransactionsWithFilter(
        queryDirection: Query.Direction?,
        startDate: Long?,
        endDate: Long?,
        trxType: String?,
        callback: (List<Transaction>) -> Unit
    ) = transactionRepository.observeTransactionsWithFilter(
        queryDirection, startDate, endDate, trxType, callback
    )

    override suspend fun getTransactionById(id: String) =
        transactionRepository.getTransactionById(id)

    override fun processTransactionDocumentSnapshot(documentSnapshot: DocumentSnapshot) =
        transactionRepository.processTransactionDocumentSnapshot(documentSnapshot)

    override fun getTotalAmountTransactions(listTransactions: List<Transaction>) =
        transactionRepository.getTotalAmountTransactions(listTransactions)


    override fun getMerchantId() = transactionRepository.getMerchantId()

    override fun getTransactionCount() = transactionRepository.getTransactionCount()

    override fun saveTransactionCount(count: Int) =
        transactionRepository.saveTransactionCount(count)

    override fun stopObserving() = transactionRepository.stopObserving()
}