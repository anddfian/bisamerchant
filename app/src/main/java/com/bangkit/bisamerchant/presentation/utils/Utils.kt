package com.bangkit.bisamerchant.presentation.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.bangkit.bisamerchant.R
import com.bangkit.bisamerchant.domain.home.model.MessageNotif
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
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
            .setContentTitle(message.title)
            .setContentText(message.body).setSubText(message.subText)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_logo_colorized))
            .setStyle(NotificationCompat.BigTextStyle())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL).setSound(defaultSoundUri)
            .setFullScreenIntent(contentIntent, true)
            .setContentIntent(contentIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = channelName
            channel.enableVibration(true)
            mBuilder.setChannelId(channelId)
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()
        mNotificationManager.notify(notificationId, notification)
    }

    fun layoutToBitmap(view: CardView): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun invoiceSharedBitmap(background: Bitmap, item: Bitmap): Bitmap {
        val merge = Bitmap.createBitmap(background.width, background.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(merge)
        canvas.drawBitmap(background, 0f, 0f, null)

        val x = (background.width - item.width) / 2
        val y = (background.height - item.height) / 2
        canvas.drawBitmap(item, x.toFloat(), y.toFloat(), null)
        return merge
    }

    fun QRSharedBitmap(background: Bitmap, item: Bitmap): Bitmap {
        val merge = Bitmap.createBitmap(background.width, background.height, Bitmap.Config.ARGB_8888)
        val scale = Bitmap.createScaledBitmap(item, item.width * 2, item.height * 2, false)
        val canvas = Canvas(merge)
        canvas.drawBitmap(background, 0f, 0f, null)

        val x = (background.width - scale.width) / 2
        val y = (background.height - scale.height) / 2
        canvas.drawBitmap(scale, x.toFloat(), y.toFloat(), null)
        return merge
    }

    fun bitmapToTempFile(context: Context, bitmap: Bitmap): Uri? {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "temp_image.png")

        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            outputStream?.close()
        }

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun truncateString(string: String, maxLength: Int): String {
        return if (string.length > maxLength) {
            string.substring(0, maxLength) + "..."
        } else {
            string
        }
    }

    fun isValidQR(inputString: String): Boolean {
        val regexPattern = Regex(pattern = "^DANA#CPM#[A-Za-z0-9 ]+$")
        return regexPattern.matches(inputString)
    }
}