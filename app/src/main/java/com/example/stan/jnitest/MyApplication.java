package com.example.stan.jnitest;

import android.app.Application;

import com.example.stan.jnitest.utils.CrashHandler;

/**
 * @Author Stan
 * @Description
 * @Date 2023/3/29 16:53
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }
}
