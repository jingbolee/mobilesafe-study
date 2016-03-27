package com.lijingbo.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import com.lijingbo.mobilesafe.activity.PwdActivity;
import com.lijingbo.mobilesafe.db.dao.LockPackagenameDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 开门狗服务，监听当前运行的程序是不是被锁定的程序
 */
public class WatchDogService extends Service {

    private LockPackagenameDao dao;
    private List< String > appLockNames;
    private TempStop tempStop;
    private String temStopPackagename;
    private boolean flags;
    private Intent intent;
    private ScreenOff screenOff;
    private DataChanged dataChanged;

    public WatchDogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class ScreenOff extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            temStopPackagename = null;
        }
    }

    class TempStop extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            temStopPackagename = intent.getStringExtra("packageName");
        }
    }

    class DataChanged extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            appLockNames = dao.findAll();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        System.out.println("开门狗程序开启了.........................");
        dao = new LockPackagenameDao(this);
        appLockNames = dao.findAll();
        //注册广播，获取到临时停止保护的应用包名
        tempStop = new TempStop();
        registerReceiver(tempStop, new IntentFilter("com.lijingbo.mobilesafe.tempstop"));
        screenOff = new ScreenOff();
        registerReceiver(screenOff, new IntentFilter("android.intent.action.SCREEN_OFF"));
        dataChanged = new DataChanged();
        registerReceiver(dataChanged, new IntentFilter("com.lijingbo.mobilesafe.datachanged"));
        flags = true;
        intent = new Intent(WatchDogService.this, PwdActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while ( flags ) {
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//                    List< ActivityManager.AppTask > appTasks = am.getAppTasks();
                    String packageName;
                    //判断SDK版本是否小于21
                    if ( Build.VERSION.SDK_INT < 21 ) {
                        packageName = getTasksInfo(am);
                    } else {
                        packageName = getProcessInfo(am);
                    }
                    if ( appLockNames.contains(packageName) ) {
                        if ( packageName.equals(temStopPackagename) ) {

                        } else {
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);
                        }
                    }
                    SystemClock.sleep(20);
                }
            }
        }).start();
    }

    //在SDK>=21的版本上获取当前处于前台的应用程序
    private String getProcessInfo(ActivityManager am) {
        List< String > activePackages = new ArrayList<>();
        List< ActivityManager.RunningAppProcessInfo > runningAppProcesses = am.getRunningAppProcesses();
        for ( ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses ) {
            if ( processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND ) {
                activePackages.addAll(Arrays.asList(processInfo.pkgList));
            }
        }
        return activePackages.get(0);
    }

    private String getTasksInfo(ActivityManager am) {
        List< ActivityManager.RunningTaskInfo > runningTasks = am.getRunningTasks(1);
        return runningTasks.get(0).topActivity.getPackageName();
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(tempStop);
        tempStop = null;
        unregisterReceiver(screenOff);
        screenOff = null;
        unregisterReceiver(dataChanged);
        dataChanged = null;
        flags = false;    //停止子线程运行
        super.onDestroy();
    }
}
