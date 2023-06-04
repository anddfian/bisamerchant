package com.bangkit.bisamerchant.data.utils

import java.text.DecimalFormat
import java.text.NumberFormat
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

    fun currencyFormat(money: Long?): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return formatter.format(money).replace(',', '.')
    }
}