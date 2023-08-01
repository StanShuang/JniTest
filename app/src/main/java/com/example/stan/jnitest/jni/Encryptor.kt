package com.example.stan.jnitest.jni

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.nio.Buffer
import java.nio.ByteBuffer
import kotlin.experimental.and


object Encryptor {
    init {
        System.loadLibrary("encryptor_lib")
    }

    private const val TAG = "JNI_TEST"
    private const val fileName = "split_test.txt"
    const val mergeFileName = "split_test_merged.txt"
    const val splitFileFormat = "split_test_%d.txt"
    const val splitCount = 4


    external fun createFile(normalPath: String)

    external fun encryption(normalPath: String, encryptPath: String)

    external fun decryption(encryptPath: String, decryptPath: String)

    private external fun split(path: String, pathPattern: String, splitCount: Int)

    private external fun merge(pathMerge: String, pathPattern: String, count: Int)

    external fun listDirAllFile(dirPath: String)

    private external fun passBitmap(bitmap: Bitmap)

    fun splitFile(baseFileUrl: String) {
        val filePath = baseFileUrl + fileName
        Log.e(TAG, "filePath = $filePath")
        val file = File(filePath)
        if (!file.exists()) {
            Log.e(TAG, "开始创建文件")
            createFile(filePath)
        }
        val pathPattern = baseFileUrl + splitFileFormat
        split(filePath, pathPattern, splitCount)
        Log.e(TAG, "文件拆分成功");
    }

    fun mergeFile(baseFileUrl: String) {
        val mergePath: String = baseFileUrl + mergeFileName
        merge(mergePath, baseFileUrl + splitFileFormat, splitCount)
        Log.e(TAG, "文件合并成功")
    }

    fun getPassBitmap() {
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            bitmap.eraseColor(0xff336699)
        }else{
            bitmap.eraseColor(Color.parseColor("#ff336699"))
        }
        val bytes = ByteArray(bitmap.width * bitmap.height * 4)
        val dst = ByteBuffer.wrap(bytes)
        bitmap.copyPixelsToBuffer(dst)
        // ARGB_8888 真实的存储顺序是 R-G-B-A
        Log.d(TAG, "R: " + Integer.toHexString((bytes[0].toInt() and (0xff))))
        Log.d(TAG, "G: " + Integer.toHexString((bytes[1].toInt() and (0xff))))
        Log.d(TAG, "B: " + Integer.toHexString((bytes[2].toInt() and (0xff))))
        Log.d(TAG, "A: " + Integer.toHexString((bytes[3].toInt() and (0xff))))

        passBitmap(bitmap)
    }


}