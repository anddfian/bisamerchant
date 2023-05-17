package com.bangkit.bisamerchant.helper

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date


object Utils {
    fun currencyFormat(money: Long?): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return formatter.format(money).replace(',', '.')
    }

    fun getCurrentDate(): String {
        val simpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy")
        return simpleDateFormat.format(Date())
    }

    fun getTodayTimestamp(): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateString = dateFormat.format(Date())
        val dateParsed = dateFormat.parse(dateString)
        return dateParsed.time / 1000
    }

    fun simpleDateFormat(date: Long): String? {
        return try {
            val simpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
            val netDate = Date(date * 1000)
            simpleDateFormat.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    fun simpleDateFormatToday(date: Long): String? {
        return try {
            val simpleDateFormat = SimpleDateFormat("HH:mm")
            val netDate = Date(date * 1000)
            simpleDateFormat.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }
}