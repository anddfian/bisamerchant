package com.bangkit.bisamerchant.data.profile.repository

import com.bangkit.bisamerchant.data.profile.datasource.ProfileDataSource
import com.bangkit.bisamerchant.domain.profile.repository.IProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileDataSource: ProfileDataSource,
) : IProfileRepository {
    override suspend fun getMerchantActive() =
        profileDataSource.getMerchantActive()

    override suspend fun getTotalTransactionsFromLastMonth(): Flow<Long> =
        profileDataSource.getTotalTransactionsFromLastMonth()
}