package com.bangkit.bisamerchant.domain.profile.repository

import com.bangkit.bisamerchant.domain.profile.model.Merchant
import com.google.firebase.firestore.ListenerRegistration

interface IProfileRepository {
    suspend fun getMerchantActive(callback: (Merchant) -> Unit): ListenerRegistration
}