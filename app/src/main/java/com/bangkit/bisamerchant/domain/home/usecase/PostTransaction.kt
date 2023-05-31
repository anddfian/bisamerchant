package com.bangkit.bisamerchant.domain.home.usecase

import com.bangkit.bisamerchant.domain.home.model.Payment
import com.bangkit.bisamerchant.domain.home.repository.IHomeRepository
import javax.inject.Inject

class PostTransaction @Inject constructor(private val homeRepository: IHomeRepository) {

    suspend fun execute(payment: Payment) =
        homeRepository.postTransaction(payment)
}