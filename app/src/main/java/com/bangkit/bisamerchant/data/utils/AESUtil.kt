package com.bangkit.bisamerchant.data.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESUtil {

    private const val AES_KEY = "0123456789abcdef"
    private const val AES_IV = "abcdef0123456789"

    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val key = SecretKeySpec(AES_KEY.toByteArray(), "AES")
        val iv = IvParameterSpec(AES_IV.toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray())
        val base64Encoded = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        return base64Encoded.replace("\n", "")
    }

    fun decrypt(encryptedText: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val key = SecretKeySpec(AES_KEY.toByteArray(), "AES")
        val iv = IvParameterSpec(AES_IV.toByteArray())
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val decodedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes)
    }
}