package com.bangkit.bisamerchant.domain.home.model

data class Transaction(
	val amount: Long,
	val trxType: String? = null,
	val id: String? = null,
	val timestamp: Long? = null
)
