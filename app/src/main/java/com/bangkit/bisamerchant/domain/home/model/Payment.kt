package com.bangkit.bisamerchant.domain.home.model

data class Payment(
    val amount: Long,
    val bankAccountNo: Long? = null,
    val bankInst: String? = null,
    val id: Long? = null,
    val merchantId: String? = null,
    val payerId: String? = null,
    val timestamp: Long? = null,
    val trxType: String = "PAYMENT"
)
