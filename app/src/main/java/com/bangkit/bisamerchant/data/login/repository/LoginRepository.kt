package com.bangkit.bisamerchant.data.login.repository

import com.bangkit.bisamerchant.data.login.datasource.LoginDatasource
import com.bangkit.bisamerchant.domain.login.repository.ILoginRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val loginDatasource: LoginDatasource
) : ILoginRepository {

    override suspend fun login(email: String, password: String) =
        loginDatasource.login(email, password)

    override suspend fun resetPassword(email: String) =
        loginDatasource.resetPassword(email)

}