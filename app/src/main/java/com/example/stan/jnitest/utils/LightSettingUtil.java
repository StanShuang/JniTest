package com.example.stan.jnitest.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

/**
 * @Author Stan
 * @Description
 * @Date 2023/8/14 15:12
 */
public class LightSettingUtil {
    private final Activity mActivity;
    //屏幕最大亮度
    float maxLight;
    //屏幕当前亮度
    public float currentLight;
    //控制屏幕亮度
    Handler handler;
    //延迟时间
    public long danyTime = 20 * 1000L;
    //休眠任务
    Runnable sleepWindowTask = () -> setWindowLight(1);
    private long lastTime;

    public LightSettingUtil(Activity activity) {
        this.mActivity = activity;
        handler = new android.os.Handler(Looper.myLooper());
        maxLight = getLightness(activity);
    }

    private float getLightness(Activity activity) {
        WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
        return localLayoutParams.screenBrightness;
    }

    private void setWindowLight(int light) {
        if (light == 1) {
            Log.d("MainActivity", "current light is 1..");
        }
        currentLight = light;
        long time = System.currentTimeMillis() - lastTime;
        if (light == 1 && time < danyTime) {
            Log.d("MainActivity", "距离上次点击还不超过规定时间");
            startSleepTask(danyTime - time);
        } else {
            WindowManager.LayoutParams localLayoutParams = mActivity.getWindow().getAttributes();
            localLayoutParams.screenBrightness = light / 255.0F;
            mActivity.getWindow().setAttributes(localLayoutParams);
        }

    }

    public void startSleepTask(long currentDanyTime) {
        setWindowLight((int) maxLight);
        handler.removeCallbacks(sleepWindowTask);
        handler.postDelayed(sleepWindowTask, currentDanyTime);
    }

    public void stopSleepTask() {
        handler.removeCallbacks(sleepWindowTask);
    }

    public void setLastTimeOnTouch() {
        lastTime = System.currentTimeMillis();
    }
}
