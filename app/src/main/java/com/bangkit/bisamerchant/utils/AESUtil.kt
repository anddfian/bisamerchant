package com.bangkit.bisamerchant.utils

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
import java.security.SecureRandom

object AESUtil {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    private const val ENCRYPTION_KEY = "YOUR_ENCRYPTION_KEY" // Replace with your encryption key
    private const val IV = "YOUR_INITIALIZATION_VECTOR" // Replace with your initialization vector
    private const val ITERATION_COUNT = 10000
    private const val KEY_LENGTH = 256

    fun encrypt(text: String): String {
        val secretKey: SecretKey = generateSecretKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decrypt(encryptedText: String): String {
        val secretKey: SecretKey = generateSecretKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes)
    }

    private fun generateSecretKey(): SecretKey {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(ENCRYPTION_KEY.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
        val secretKeyBytes = secretKeyFactory.generateSecret(spec).encoded
        return SecretKeySpec(secretKeyBytes, ALGORITHM)
    }
}

