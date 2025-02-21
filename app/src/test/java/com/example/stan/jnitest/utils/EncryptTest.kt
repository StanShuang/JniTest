package com.example.stan.jnitest.utils

import com.example.stan.jnitest.utils.test.DateUtils
import org.junit.Assert
import org.junit.Test

/**
 *@Author Stan
 *@Description
 *@Date 2025/2/20 16:43
 */
class EncryptTest {
    @Test
    fun encryptFun_test() {
        var size = 16
        val str16 = EncryptUtils.generate16DigitHexString(size)
        val str32 = EncryptUtils.generate32DigitHexString()
        val md5Hash = EncryptUtils.md5Hash(str16)
        println("str16:${str16}----str32:${str32}-----str16 to md5:${md5Hash}")
        Assert.assertEquals(32, md5Hash.length)
        Assert.assertNotNull("md5Hash is not none", md5Hash)
    }

    @Test
    fun getValidData_test() {
        val time1 = System.currentTimeMillis()
        val validData1 = DateUtils.getValidData(-1)
        Assert.assertEquals(time1, validData1)
        val time2 = System.currentTimeMillis() - 100000
        Assert.assertEquals(time2, DateUtils.getValidData(time2))
    }
}