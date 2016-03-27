package com.lijingbo.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.lijingbo.mobilesafe.receiver.AutoKillTaskReceiver;

public class AutoKillTaskService extends Service {

    private AutoKillTaskReceiver autoKillTaskReceiver;

    public AutoKillTaskService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_OFF");
        autoKillTaskReceiver = new AutoKillTaskReceiver();
        registerReceiver(autoKillTaskReceiver, filter);
        System.out.println("注册了AutoKillTaskReceiver广播");
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(autoKillTaskReceiver);
        System.out.println("取消了AutoKillTaskReceiver广播");
        super.onDestroy();
    }


}
