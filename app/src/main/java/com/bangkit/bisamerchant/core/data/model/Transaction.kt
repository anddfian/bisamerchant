package com.bangkit.bisamerchant.core.data.model

import com.google.gson.annotations.SerializedName

data class Transaction(

	@field:SerializedName("amount")
	val amount: Long,

	@field:SerializedName("trxType")
	val trxType: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("timestamp")
	val timestamp: Long? = null
)
