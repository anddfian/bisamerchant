package com.bangkit.bisamerchant.core.domain.usecase

import com.bangkit.bisamerchant.core.data.model.Merchant
import com.bangkit.bisamerchant.core.domain.repository.IMerchantRepository
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class MerchantInteractor @Inject constructor(private val merchantRepository: IMerchantRepository) :
    MerchantUseCase {
    override suspend fun observeMerchantActive(callback: (Merchant) -> Unit) =
        merchantRepository.observeMerchantActive(callback)

    override suspend fun observeMerchants(callback: (List<Merchant>) -> Unit) =
        merchantRepository.observeMerchants(callback)

    override fun stopObserving() =
        merchantRepository.stopObserving()

    override fun processMerchantActiveQuerySnapshot(querySnapshot: QuerySnapshot) =
        merchantRepository.processMerchantActiveQuerySnapshot(querySnapshot)

    override fun processMerchantsQuerySnapshot(querySnapshot: QuerySnapshot) =
        merchantRepository.processMerchantsQuerySnapshot(querySnapshot)

    override suspend fun changeMerchantStatus(id: String?) =
        merchantRepository.changeMerchantStatus(id)

    override fun getMerchantId() =
        merchantRepository.getMerchantId()

    override fun saveMerchantId(id: String) =
        merchantRepository.saveMerchantId(id)

    override fun saveAmountHide(hide: Boolean) =
        merchantRepository.saveAmountHide(hide)

    override fun getAmountHide() =
        merchantRepository.getAmountHide()

    override fun deleteMerchant() =
        merchantRepository.deleteMerchant()
}