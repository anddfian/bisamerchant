package com.bangkit.bisamerchant.domain.register.usecase

import com.bangkit.bisamerchant.domain.register.repository.IRegisterRepository
import javax.inject.Inject

class RegisterOwner @Inject constructor(
    private val registerRepository: IRegisterRepository
) {

    suspend fun execute(name: String, email: String, password: String, pin: String) =
        registerRepository.registerOwner(name, email, password, pin)
}