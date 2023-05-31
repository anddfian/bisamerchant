package com.bangkit.bisamerchant.domain.history.model

data class Transaction(
	val amount: Long,
	val trxType: String? = null,
	val id: String? = null,
	val timestamp: Long? = null
)
