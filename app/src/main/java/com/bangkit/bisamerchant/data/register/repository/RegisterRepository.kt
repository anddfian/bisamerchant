package com.bangkit.bisamerchant.data.register.repository

import com.bangkit.bisamerchant.data.register.datasource.RegisterDataSource
import com.bangkit.bisamerchant.domain.register.repository.IRegisterRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterRepository @Inject constructor(
    private val registerDataSource: RegisterDataSource
) : IRegisterRepository{
    override suspend fun registerOwner(name: String, email: String, password: String, pin: String) =
        registerDataSource.registerOwner(name, email, password, pin)
}