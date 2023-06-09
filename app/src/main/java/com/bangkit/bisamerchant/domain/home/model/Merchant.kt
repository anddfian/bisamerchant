package com.bangkit.bisamerchant.domain.home.model

data class Merchant(
	val id: String? = null,
	val balance: Long? = null,
	val merchantActive: Boolean? = null,
	val merchantLogo: String? = null,
	val merchantAddress: String? = null,
	val merchantType: String? = null,
	val email: String? = null,
	val merchantName: String? = null,
	val tokenId: String? = null
)
