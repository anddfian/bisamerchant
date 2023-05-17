package com.bangkit.bisamerchant.data.response

import com.google.gson.annotations.SerializedName

data class DetailTransaction(

	@field:SerializedName("amount")
	val amount: Long? = null,

	@field:SerializedName("merchantId")
	val merchantId: String? = null,

	@field:SerializedName("payerId")
	val payerId: String? = null,

	@field:SerializedName("bankAccountNo")
	val bankAccountNo: Long? = null,

	@field:SerializedName("bankInst")
	val bankInst: String? = null,

	@field:SerializedName("trxType")
	val trxType: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("timestamp")
	val timestamp: Long? = null
)
