package com.bangkit.bisamerchant.data.home.repository

import com.bangkit.bisamerchant.data.home.datasource.HomeDataSource
import com.bangkit.bisamerchant.domain.home.model.DetailTransaction
import com.bangkit.bisamerchant.domain.home.model.Merchant
import com.bangkit.bisamerchant.domain.home.model.Transaction
import com.bangkit.bisamerchant.domain.home.repository.IHomeRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val homeDataSource: HomeDataSource
) : IHomeRepository {
    override suspend fun getMerchantActive(callback: (Merchant) -> Unit) =
        homeDataSource.getMerchantActive(callback)

    override suspend fun getMerchants() =
        homeDataSource.getMerchants()

    override suspend fun updateMerchantStatus(id: String?) =
        homeDataSource.updateMerchantStatus(id)

    override suspend fun updateMerchantId(id: String) =
        homeDataSource.updateMerchantId(id)

    override suspend fun updateHideAmount(hide: Boolean) =
        homeDataSource.updateHideAmount(hide)

    override suspend fun getHideAmount() =
        homeDataSource.getHideAmount()

    override fun getTotalAmountTransactions(listTransactions: List<Transaction>): Long =
        homeDataSource.getTotalAmountTransactions(listTransactions)

    override suspend fun postTransaction(detailTransaction: DetailTransaction): Flow<String> =
        flow {
            var fee = 0L
            val currentBalance = if (detailTransaction.trxType == "PAYMENT") {
                detailTransaction.payerId?.let { homeDataSource.getPayerBalance(it) }
            } else {
                detailTransaction.merchantId?.let { homeDataSource.getMerchantBalance(it) }
            }

            if (currentBalance != null) {
                if (currentBalance >= detailTransaction.amount) {

                    if (detailTransaction.trxType == "MERCHANT_WITHDRAW") {
                        val validationMessage =
                            homeDataSource.validateWithdrawAmount(detailTransaction.amount)

                        if (validationMessage != AMOUNT_VALIDATED) {
                            emit(validationMessage)
                            return@flow
                        }

                        fee = homeDataSource.getTransactionFee(detailTransaction.amount)
                    }

                    val newBalance = currentBalance - detailTransaction.amount - fee
                    homeDataSource.postTransaction(detailTransaction, newBalance, fee)
                        .collect { result ->
                            emit(result)
                        }
                } else {
                    emit("Insufficient money")
                }

            } else {
                emit("User not found")
            }

        }.catch { e ->
            emit(e.message.toString())
        }.flowOn(Dispatchers.IO)

    override suspend fun getTransactionsToday(callback: (List<Transaction>) -> Unit): ListenerRegistration =
        homeDataSource.getTransactionsToday(callback)

    override suspend fun getMerchantId() =
        homeDataSource.getMerchantId()

    override suspend fun validateWithdrawAmount(amount: Long): Flow<String> = flow {
        val result = homeDataSource.validateWithdrawAmount(amount)
        emit(result)
    }

    override suspend fun getTransactionFee(amount: Long): Flow<Long> = flow {
        emit(homeDataSource.getTransactionFee(amount))
    }

    companion object {
        private const val AMOUNT_VALIDATED = "Enter PIN"
    }
}