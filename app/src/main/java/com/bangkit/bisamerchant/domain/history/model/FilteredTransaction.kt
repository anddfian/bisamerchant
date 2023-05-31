package com.bangkit.bisamerchant.domain.history.model

import com.google.firebase.firestore.Query

data class FilteredTransaction(
	val queryDirection: Query.Direction? = Query.Direction.ASCENDING,
	val startDate: Long? = 0,
	val endDate: Long?,
	val trxType: String?,
)
