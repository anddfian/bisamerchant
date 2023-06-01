package com.bangkit.bisamerchant.domain.login.usecase

import com.bangkit.bisamerchant.domain.login.repository.ILoginRepository
import javax.inject.Inject

class ResetPassword @Inject constructor(
    private val loginRepository: ILoginRepository
) {

    suspend fun execute(email: String) =
        loginRepository.resetPassword(email)
}