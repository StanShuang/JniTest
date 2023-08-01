package com.example.stan.jnitest.jni

import kotlin.random.Random as Random

class JniNative {

    init {
        System.loadLibrary("jnitest")
    }

    val showText: String = "Hello World"

    external fun stringFromJNI(): String

    external fun accessField()

    external fun accessMethod(): String

    external fun accessStaticMethod(max: Int): Int

    private external fun getIntArray(length: Int): IntArray

    private external fun sortIntArray(array: IntArray)

    fun getAuthName(name: String): String {
        return "$name-Hello NDK"
    }

    companion object {
        @JvmStatic
        fun getRandomValue(max: Int): Int {
            return Random.nextInt(max)
        }
    }

    fun arrayTest() {
        val array: IntArray = getIntArray(20)
        for (i in array) {
            print("$i , ")
        }
        sortIntArray(array)
        print("After sort:")
        for (j in array){
            print("$j , ")
        }
    }


}