package com.example.stan.jnitest.jni;

import android.os.Build;

public class JniCallbackDemo {
    static {
        System.loadLibrary("jni_callback");
    }

    public static class JniHandler{
        public static String getBuildVersion() {
            return Build.VERSION.RELEASE;
        }
    }

}
