package com.example.stan.jnitest.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith

/**
 *@Author Stan
 *@Description
 *@Date 2025/2/20 16:34
 */
@RunWith(AndroidJUnit4::class)
class EncryptUtilsTest {
    @Test
    fun encryptFun_strTest() {
        var size = 16
        val str16 = EncryptUtils.generate16DigitHexString(size)
        val str32 = EncryptUtils.generate32DigitHexString()
        val md5Hash = EncryptUtils.md5Hash(str16)
        println("str16:${str16}----str32:${str32}-----str16 to md5:${md5Hash}")
    }
}