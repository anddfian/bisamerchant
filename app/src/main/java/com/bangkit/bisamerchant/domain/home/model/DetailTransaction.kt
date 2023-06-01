package com.bangkit.bisamerchant.domain.home.model

data class DetailTransaction(
	val amount: Long,
	val merchantId: String? = null,
	val payerId: String? = null,
	val bankAccountNo: Long? = null,
	val bankInst: String? = null,
	val trxType: String? = null,
	val id: String? = null,
	val timestamp: Long? = null
)
