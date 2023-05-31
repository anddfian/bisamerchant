package com.bangkit.bisamerchant.data.profile.repository

import com.bangkit.bisamerchant.data.profile.datasource.ProfileDataSource
import com.bangkit.bisamerchant.domain.profile.model.Merchant
import com.bangkit.bisamerchant.domain.profile.repository.IProfileRepository
import com.google.firebase.firestore.ListenerRegistration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileDataSource: ProfileDataSource,
) : IProfileRepository {
    override suspend fun getMerchantActive(callback: (Merchant) -> Unit): ListenerRegistration =
        profileDataSource.getMerchantActive(callback)
}