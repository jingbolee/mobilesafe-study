package com.lijingbo.mobilesafe.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.lijingbo.mobilesafe.receiver.AlarmReceiver;

/**
 * 流量记录service，启动一个定时器，每隔2分钟获取一次流量信息，存储到数据库
 */
public class TrafficListenService extends Service {

    public TrafficListenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("com.lijingbo.mobilesafe.recordtraffic");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtMillis = SystemClock.elapsedRealtime();
        //60秒启动一次
        long intervalMillis = 60 * 1000;
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
    }
}
