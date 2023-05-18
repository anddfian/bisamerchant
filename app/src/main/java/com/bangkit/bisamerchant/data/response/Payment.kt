package com.bangkit.bisamerchant.data.response

import com.google.gson.annotations.SerializedName

data class Payment(
    @field:SerializedName("amount")
    val amount: Long,

    @field:SerializedName("id")
    val id: Long? = null,

    @field:SerializedName("merchantId")
    val merchantId: String? = null,

    @field:SerializedName("payerId")
    val payerId: String,

    @field:SerializedName("timestamp")
    val timestamp: Long? = null,

    @field:SerializedName("trxType")
    val trxType: String = "PAYMENT"
)
