package com.lijingbo.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.activity.TaskManagerActivity;
import com.lijingbo.mobilesafe.receiver.AutoKillTaskReceiver;
import com.lijingbo.mobilesafe.receiver.MyWidget;
import com.lijingbo.mobilesafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateWidgetService extends Service {
    private ScreenOff screenOff;
    private ScreenOn screenOn;
    private Timer timer;
    private TimerTask task;

    public UpdateWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册锁屏广播
        IntentFilter offFilter = new IntentFilter();
        offFilter.addAction("android.intent.action.SCREEN_OFF");
        screenOff = new ScreenOff();
        registerReceiver(screenOff, offFilter);
        //注册开屏广播
        IntentFilter onFilter = new IntentFilter();
        onFilter.addAction("android.intent.action.SCREEN_ON");
        screenOn = new ScreenOn();
        registerReceiver(screenOn, onFilter);
        //启动timer
        startTimer();
    }

    private void startTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
//                System.out.println("更新桌面的Widget了，3秒来一次");
                int runningTaskCount = SystemInfoUtils.getRunningTaskCount(UpdateWidgetService.this);
                long avaliMem = SystemInfoUtils.getAvaliMem(UpdateWidgetService.this);
                ComponentName componentName = new ComponentName(UpdateWidgetService.this, MyWidget.class);
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.appwidget_view);

                //设置Widget中Textview的显示内容
                remoteViews.setTextViewText(R.id.tv_runprocessnumber, "正在运行软件:" + runningTaskCount);
                remoteViews.setTextViewText(R.id.tv_avalimem, "可用内存:" + Formatter.formatFileSize(UpdateWidgetService.this, avaliMem));

                //点击widget的一键清理按钮，发送广播，在AutoKillTaskReceiver广播中杀掉所有的进程。
                Intent intent = new Intent(UpdateWidgetService.this, AutoKillTaskReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(UpdateWidgetService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.btn_killall, pendingIntent);

                //点击widget显示信息部分，跳到程序管理页面
                Intent startActivityIntent = new Intent(UpdateWidgetService.this, TaskManagerActivity.class);
                startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent processInfoIntent = PendingIntent.getActivity(UpdateWidgetService.this, 0, startActivityIntent, PendingIntent.FLAG_ONE_SHOT);
                remoteViews.setOnClickPendingIntent(R.id.ll_processinfo, processInfoIntent);

                //由AppWidgetManager处理Wiget。
                AppWidgetManager awm = AppWidgetManager.getInstance(getApplicationContext());
                awm.updateAppWidget(componentName, remoteViews);

            }
        };
        timer.schedule(task, 0, 3000);
    }

    //关屏广播
    class ScreenOff extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopTimer();
        }
    }

    //开屏广播
    class ScreenOn extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            startTimer();
        }
    }


    @Override
    public void onDestroy() {
        stopTimer();
        unregisterReceiver(screenOff);
        unregisterReceiver(screenOn);
        screenOn = null;
        screenOff = null;
        super.onDestroy();

    }

    private void stopTimer() {
        if ( timer != null && task != null ) {
            timer.cancel();
            task.cancel();
            timer = null;
            task = null;
        }
    }
}
