package com.example.stan.jnitest.android.view;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.stan.jnitest.R;

/**
 * @Author Stan
 * @Description
 * @Date 2023/3/13 14:04
 */
public class MyAppWidgetProvider extends AppWidgetProvider {
    private static final String CLICK_ACTION = "com.example.stan.jnitest.android.view.action.CLICK";

    public MyAppWidgetProvider() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i("MyAppWidgetProvider", "onReceive : Action = " + intent.getAction());
        //点击桌面小部件，添加一个动画效果
        if (intent.getAction().equals(CLICK_ACTION)) {
            new Thread(() -> {
                Bitmap srcBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pic_appwidget);
                AppWidgetManager appWidgetProvider = AppWidgetManager.getInstance(context);
                RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget);
                remoteView.setImageViewResource(R.id.img_appwidget, R.drawable.pic_appwidget_click);
                Intent clickIntent = new Intent();
                clickIntent.setAction(CLICK_ACTION);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
                remoteView.setOnClickPendingIntent(R.id.img_appwidget, pendingIntent);
                appWidgetProvider.updateAppWidget(new ComponentName(context, MyAppWidgetProvider.class), remoteView);
                SystemClock.sleep(30);
            }).start();
        }
    }

    /**
     * 每次桌面小部件更新时
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        final int counter = appWidgetIds.length;
        Log.i("MyAppWidgetProvider", "counter = " + counter);
        for (int i = 0; i < counter; i++) {
            int appWidgetId = appWidgetIds[i];
            onWidgetUpdate(context, appWidgetManager, appWidgetId);
        }
    }

    private void onWidgetUpdate(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent clickIntent = new Intent();
        clickIntent.setAction(CLICK_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
        remoteView.setOnClickPendingIntent(R.id.img_appwidget, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, remoteView);
    }

    private Bitmap rotateBitmap(Context context, Bitmap srcBitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degree);
        return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
    }
}
