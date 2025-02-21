package com.example.stan.jnitest.recorder;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.MEDIA_PROJECTION_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


/**
 * @Author Stan
 * @Description
 * @Date 2024/8/28 11:28
 */
public class RecorderSetting {
    //请求码
    private final static int REQUEST_CODE = 101;
    //权限请求码
    private final static int PERMISSION_REQUEST_CODE = 1101;
    private final Activity mActivity;

    //录屏服务
    ScreenRecordService screenRecordService;
    //录屏工具
    MediaProjectionManager mediaProjectionManager;
    MediaProjection mediaProjection;
    //获取录屏范围参数
    DisplayMetrics metrics;

    private Intent captureIntent;

    public RecorderSetting(Activity activity) {
        this.mActivity = activity;
    }

    //权限检查，连接录屏服务
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            //调用检查权限接口进行权限检查
            if ((ContextCompat.checkSelfPermission(mActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)) {
                //如果没有权限，获取权限
                //调用请求权限接口进行权限申请
                ActivityCompat.requestPermissions(mActivity, new String[]{
                        Manifest.permission.POST_NOTIFICATIONS
                }, PERMISSION_REQUEST_CODE);
            } else {
                //有权限，连接录屏服务，进行录屏
                if (screenRecordService != null) {
                    //将请求码作为标识一起发送，调用该接口，需有返回方法
                    mActivity.startActivityForResult(captureIntent, REQUEST_CODE);
                } else {
                    connectService();
                }
            }
        } else if (Build.VERSION.SDK_INT < 30) {
            //调用检查权限接口进行权限检查
            if ((ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                //如果没有权限，获取权限
                //调用请求权限接口进行权限申请
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);
            } else {
                //有权限，连接录屏服务，进行录屏
                if (screenRecordService != null) {
                    //将请求码作为标识一起发送，调用该接口，需有返回方法
                    mActivity.startActivityForResult(captureIntent, REQUEST_CODE);
                } else {
                    connectService();
                }
            }
        } else {
            if (screenRecordService != null) {
                //将请求码作为标识一起发送，调用该接口，需有返回方法
                mActivity.startActivityForResult(captureIntent, REQUEST_CODE);
            } else {
                connectService();
            }
        }

    }

    //没有权限，去请求权限后，需要判断用户是否同意权限请求
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            //请求码相同
//            if (grantResults.length > 1 && ((grantResults[0] != PackageManager.PERMISSION_GRANTED) || (grantResults[1] != PackageManager.PERMISSION_GRANTED))) {
//                //如果结果都存在，但是至少一个没请求成功，弹出提示
//                Toast.makeText(mActivity, "请同意必须的应用权限，否则无法正常使用该功能！", Toast.LENGTH_SHORT).show();
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mActivity, "请同意必须的应用权限，否则无法正常使用该功能！", Toast.LENGTH_SHORT).show();
            } else {
                //如果结果都存在，两个权限都申请成功，连接服务，启动录屏
                Toast.makeText(mActivity, "权限申请成功，用户同意！", Toast.LENGTH_SHORT).show();
                connectService();
            }
        }
    }

    //连接服务
    public void connectService() {
        //通过intent为中介绑定Service，会自动create
        Intent intent = new Intent(mActivity, ScreenRecordService.class);
        //绑定过程连接，选择绑定模式
        mActivity.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    //连接服务成功与否，具体连接过程
    //调用连接接口，实现连接，回调连接结果
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //服务连接成功，需要通过Binder获取服务，达到Activity和Service通信的目的
            //获取Binder
            ScreenRecordService.ScreenRecordBinder binder = (ScreenRecordService.ScreenRecordBinder) iBinder;
            //通过Binder获取Service
            screenRecordService = binder.getScreenRecordService();
            //获取到服务，初始化录屏管理者
            mediaProjectionManager = (MediaProjectionManager) mActivity.getSystemService(MEDIA_PROJECTION_SERVICE);
            //通过管理者，创建录屏请求，通过Intent
            captureIntent = mediaProjectionManager.createScreenCaptureIntent();
            //将请求码作为标识一起发送，调用该接口，需有返回方法
            mActivity.startActivityForResult(captureIntent, REQUEST_CODE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //连接失败
            Toast.makeText(mActivity, "录屏服务未连接成功，请重试！", Toast.LENGTH_SHORT).show();
        }
    };

    //返回方法，获取返回的信息
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //首先判断请求码是否一致，结果是否ok
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //录屏请求成功，使用工具MediaProjection录屏
            //从发送获得的数据和结果中获取该工具
            //录屏逻辑
            screenRecordService.createNotification();
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);//必须在通知显示之后调用
            MediaProjectionCallback mMediaProjectionCallback = new MediaProjectionCallback();
            mediaProjection.registerCallback(mMediaProjectionCallback, new Handler());
            //将该工具给Service，并一起传过去需要录制的屏幕范围的参数
            if (screenRecordService != null) {
                screenRecordService.setMediaProjection(mediaProjection);
                //获取录屏屏幕范围参数
                metrics = new DisplayMetrics();
                mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                screenRecordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
                new Handler().postDelayed(() -> screenRecordService.startRecord(), 1000);

            }
        } else if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            //未授予权限
        }
    }

    public void startRecorder() {
        //点击请求录屏后，第一件事，检查权限
        checkPermission();
    }

    public void stopRecorder() {
        if (screenRecordService != null && !screenRecordService.isRunning()) {
            //没有在录屏，无法停止，弹出提示
            Toast.makeText(mActivity, "您还没有录屏，无法停止，请先开始录屏吧！", Toast.LENGTH_SHORT).show();
        } else if (screenRecordService != null && screenRecordService.isRunning()) {
            //正在录屏，点击停止，停止录屏
            screenRecordService.stopRecord();
        }
    }

    //当应用结束的时候，需要解除绑定服务，防止造成内存泄漏
    public void onDestroy() {
        mActivity.unbindService(serviceConnection);
    }

    //Called when the MediaProjection session is no longer valid.
    private static class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {

        }
    }

}
