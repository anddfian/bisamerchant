package com.bangkit.bisamerchant.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.data.response.MessageNotif
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
        content: String, width: Int = 210, height: Int = 210, margin: Int = 0
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

    fun sendNotification(
        context: Context,
        contentIntent: PendingIntent,
        channelId: String,
        channelName: String,
        notificationId: Int,
        resources: Resources,
        message: MessageNotif
    ) {
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val mBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_logo_colorized)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_logo_colorized))
            .setStyle(NotificationCompat.BigTextStyle()).setContentTitle(message.title)
            .setContentText(message.body).setSubText(message.subText)
            .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL).setSound(defaultSoundUri)
            .setContentIntent(contentIntent).setFullScreenIntent(contentIntent, true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            )
            mBuilder.setChannelId(channelId)
            channel.description = channelName
            channel.enableVibration(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()
        mNotificationManager.notify(notificationId, notification)
    }


}