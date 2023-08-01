package com.example.stan.jnitest.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @Author Stan
 * @Description 异常处理器
 * @Date 2023/3/29 15:59
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "JniTest::CrashHandler";
    private static final CrashHandler instance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;
    public CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return instance;
    }

    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }


    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Log.w(TAG, e.getMessage());
        //导出信息到应用缓存中
        try {
            dumpExceptionToCache(e);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        //上传异常信息到服务器
        uploadExceptionToServer();
        e.printStackTrace();
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(t, e);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    private void dumpExceptionToCache(Throwable ex) throws IOException {
        String path = mContext.getExternalCacheDir().getPath() + "CrashTest/log/";
        String FILE_NAME = "crash";
        String FILE_NAME_SUFFIX = ".trace";
        Log.d(TAG, "cache path= " + path);
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        long current = System.currentTimeMillis();
        @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        File file = new File(path + FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            printWriter.println(time);
            dumpPhoneInfo(printWriter);
            printWriter.println();
            ex.printStackTrace(printWriter);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dumpPhoneInfo(PrintWriter printWriter) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        printWriter.print("App Version: ");
        printWriter.print(packageInfo.versionName);
        printWriter.print("_");
        printWriter.println(packageInfo.versionCode);

        printWriter.print("OS Version: ");
        printWriter.print(Build.VERSION.RELEASE);
        printWriter.print("_");
        printWriter.println(Build.VERSION.SDK_INT);

        printWriter.print("Model: ");
        printWriter.println(Build.MODEL);

        printWriter.print("CPU ABI: ");
        printWriter.println(Build.CPU_ABI);

    }

    private void uploadExceptionToServer() {
        //
    }
}
