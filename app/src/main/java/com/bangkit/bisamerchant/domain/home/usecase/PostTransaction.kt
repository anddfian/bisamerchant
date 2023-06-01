package com.bangkit.bisamerchant.domain.home.usecase

import com.bangkit.bisamerchant.domain.home.model.DetailTransaction
import com.bangkit.bisamerchant.domain.home.repository.IHomeRepository
import javax.inject.Inject

class PostTransaction @Inject constructor(private val homeRepository: IHomeRepository) {

    suspend fun execute(detailTransaction: DetailTransaction) =
        homeRepository.postTransaction(detailTransaction)
}