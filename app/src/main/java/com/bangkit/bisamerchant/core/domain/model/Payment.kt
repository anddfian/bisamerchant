package com.bangkit.bisamerchant.core.domain.model

data class Payment(
    val amount: Long,
    val id: Long? = null,
    val merchantId: String? = null,
    val payerId: String,
    val timestamp: Long? = null,
    val trxType: String = "PAYMENT"
)
