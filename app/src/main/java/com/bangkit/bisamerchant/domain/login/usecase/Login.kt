package com.bangkit.bisamerchant.domain.login.usecase

import com.bangkit.bisamerchant.domain.login.repository.ILoginRepository
import javax.inject.Inject

class Login @Inject constructor(private val loginRepository: ILoginRepository) {

    suspend fun execute(email: String, password: String) =
        loginRepository.login(email, password)
}