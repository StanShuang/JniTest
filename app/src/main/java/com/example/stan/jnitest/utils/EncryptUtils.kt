package com.example.stan.jnitest.utils

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Random


/**
 *@Author Stan
 *@Description
 *@Date 2025/2/17 14:14
 */
object EncryptUtils {
    fun generate16DigitHexString(size: Int): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(size) // 8 bytes → 16 hex characters
        secureRandom.nextBytes(bytes)

        val hexString = StringBuilder()
        for (b in bytes) {
            hexString.append(String.format("%02x", b)) // 小写，如需大写改用 "%02X"
        }
        return hexString.toString()
    }

    fun generate32DigitHexString(): String {
        // 创建一个Random实例
        val random: Random = Random()
        val bytes = ByteArray(16)
        val sb = java.lang.StringBuilder()
        for (i in bytes) { // 因为byte是8位，所以需要16个byte来生成32位的16进制数
            val b = random.nextInt(256).toByte() // 生成一个0到255之间的随机数，并转换为byte
            sb.append(String.format("%02x", b)) // 将byte转换为16进制并格式化为两位数，不足两位前面补0
        }
        return sb.toString()
    }

    fun md5Hash(content: String) = hashDigest(content, "MD5")

    private fun hashDigest(content: String, algorithm: String): String {
        try {
            val md5: MessageDigest = MessageDigest.getInstance(algorithm)
            val signBytes: ByteArray = md5.digest(content.toByteArray())
            val hashText = StringBuilder()
            for (b in signBytes) {
                val temp = Integer.toHexString(b.toInt() and 0xff)
                if (temp.length == 1) {
                    hashText.append(0)
                }
                hashText.append(temp)
            }
            //FcmLog.d("${algorithm}待哈希:$content")
            //FcmLog.d("${algorithm}已哈希:$hashText")
            return hashText.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

}