package com.example.stan.jnitest

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.stan.jnitest.utils.EncryptUtils

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.stan.jnitest", appContext.packageName)
    }

    @Test
    fun createGenerate16Digit_returnString(){
        val size = 16
        val str = EncryptUtils.generate16DigitHexString(size)
        println("result is:${str}")
    }
}