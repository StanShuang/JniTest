package com.example.stan.jnitest.utils.test

import java.util.Calendar
import java.util.Locale

/**
 *@Author Stan
 *@Description
 *@Date 2025/2/20 16:57
 */
object DateUtils {
    fun getValidData(milliseconds: Long): Long {
        var validData = milliseconds
        val timeInMillis = Calendar.getInstance(Locale.US).timeInMillis
        if (milliseconds <= 0) {
            validData = timeInMillis
        }
        return validData
    }
}