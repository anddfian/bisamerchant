package com.bangkit.bisamerchant.helper

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.EnumMap
import java.util.Locale


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
        val today = Calendar.getInstance()
        today.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return today.timeInMillis
    }

    fun simpleDateFormat(date: Long?, format: String): String? {
        val dateFormatted = date?.let { Date(it) }
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        return dateFormatted?.let { formatter.format(it) }
    }

    fun generateQRCode(
        content: String,
        width: Int = 210,
        height: Int = 210,
        margin: Int = 0
    ): Bitmap? {
        val hints: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.MARGIN] = margin
        try {
            val bitMatrix =
                QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) -0x1000000 else -0x1)
                }
            }
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }
}