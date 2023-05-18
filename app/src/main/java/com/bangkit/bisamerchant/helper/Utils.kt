package com.bangkit.bisamerchant.helper

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.EnumMap


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