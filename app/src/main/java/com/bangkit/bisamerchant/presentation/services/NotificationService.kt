package com.bangkit.bisamerchant.presentation.services

import com.bangkit.bisamerchant.domain.service.usecase.PostRegistrationToken
import javax.inject.Inject

class NotificationService @Inject constructor(
    private val postRegistrationToken: PostRegistrationToken,
) {
    suspend fun sendTokenRegistration(token: String) {
        postRegistrationToken.execute(token)
    }
}