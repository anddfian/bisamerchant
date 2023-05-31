package com.bangkit.bisamerchant.domain.home.usecase

import com.bangkit.bisamerchant.domain.home.model.Transaction
import com.bangkit.bisamerchant.domain.home.repository.IHomeRepository

import javax.inject.Inject

class GetTotalAmountTransactions @Inject constructor(private val homeRepository: IHomeRepository) {

    fun execute(listTransactions: List<Transaction>) =
        homeRepository.getTotalAmountTransactions(listTransactions)
}