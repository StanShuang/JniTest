package com.example.stan.jnitest

import android.app.Application
import com.example.stan.jnitest.utils.CrashHandler
import org.OpenUDID.OpenUDID_manager

/**
 * @Author Stan
 * @Description
 * @Date 2023/3/29 16:53
 */
class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val crashHandler = CrashHandler.getInstance()
        crashHandler.init(this)
        OpenUDID_manager.sync(this);
    }
}