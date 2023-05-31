package com.bangkit.bisamerchant.domain.merchantsetting.repository

import com.bangkit.bisamerchant.domain.merchantsetting.model.Merchant
import com.google.firebase.firestore.ListenerRegistration

interface IMerchantSettingRepository {
    suspend fun getMerchantActive(callback: (Merchant) -> Unit): ListenerRegistration
}