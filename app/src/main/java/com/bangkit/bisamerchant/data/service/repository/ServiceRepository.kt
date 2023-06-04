package com.bangkit.bisamerchant.data.service.repository

import com.bangkit.bisamerchant.data.service.datasource.ServiceDataSource
import com.bangkit.bisamerchant.domain.service.repository.IServiceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepository @Inject constructor(private val serviceDataSource: ServiceDataSource) : IServiceRepository {

    override suspend fun postRegistrationToken(token: String) {
        serviceDataSource.postRegistrationToken(token)
    }
}