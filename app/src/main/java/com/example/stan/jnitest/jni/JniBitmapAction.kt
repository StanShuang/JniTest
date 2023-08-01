package com.example.stan.jnitest.jni

import android.graphics.Bitmap

/**
 *@Author Stan
 *@Description
 *@Date 2023/2/8 15:37
 */
class JniBitmapAction {
    init {
        System.loadLibrary("jni_bitmap")
    }

    external fun nativeProcessBitmap(bitmap: Bitmap)
}