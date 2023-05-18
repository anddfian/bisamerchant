package com.bangkit.bisamerchant.data.response

import com.google.gson.annotations.SerializedName

data class Merchant(
	@field:SerializedName("balance")
	val balance: Long? = null,

	@field:SerializedName("merchantActive")
	val merchantActive: Boolean? = null,

	@field:SerializedName("merchantLogo")
	val merchantLogo: String? = null,

	@field:SerializedName("merchantAddress")
	val merchantAddress: String? = null,

	@field:SerializedName("merchantType")
	val merchantType: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("merchantName")
	val merchantName: String? = null,

	@field:SerializedName("transactionCount")
	val transactionCount: Long? = null
)
