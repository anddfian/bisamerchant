package com.bangkit.bisamerchant.domain.profile.model

data class Merchant(
	val id: String? = null,
	val balance: Long? = null,
	val merchantActive: Boolean? = null,
	val merchantLogo: String? = null,
	val merchantAddress: String? = null,
	val merchantType: String? = null,
	val email: String? = null,
	val merchantName: String? = null,
	val transactionCount: Long? = null
)
