package com.bangkit.bisamerchant.domain.home.model

data class MessageNotif(
	val title: String?,
	val body: String?,
	val subText: String? = "Transaction",
)
