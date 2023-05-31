package com.bangkit.bisamerchant.data.utils

import java.util.Calendar

object Utils {
    fun getTodayTimestamp(): Long {
        val today = Calendar.getInstance()
        today.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return today.timeInMillis
    }
}