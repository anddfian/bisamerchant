package com.bangkit.bisamerchant.core.helper

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

object AESUtil {

    @SuppressLint("GetInstance")
    @RequiresApi(Build.VERSION_CODES.O)
    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val key = "0123456789abcdef"
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    @SuppressLint("GetInstance")
    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(encryptedText: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val key = "0123456789abcdef"
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.getDecoder().decode(encryptedText)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes)
    }
}