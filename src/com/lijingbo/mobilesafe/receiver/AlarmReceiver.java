package com.lijingbo.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import com.lijingbo.mobilesafe.db.dao.TrafficManagerDao;
import com.lijingbo.mobilesafe.utils.LogUtils;
import com.lijingbo.mobilesafe.utils.TimeUtils;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String TAG="AlarmReceiver";
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.e(TAG,"后台定时收集数据信息的广播启动了");
        TrafficManagerDao dao = new TrafficManagerDao(context);
        PackageManager pm = context.getPackageManager();
        List< ApplicationInfo > installedApplications = pm.getInstalledApplications(0);
        for ( ApplicationInfo app : installedApplications ) {
            int uid = app.uid;
            //接收的数据流量
            long uidRxBytes = TrafficStats.getUidRxBytes(uid);
            //发送的数据流量
            long uidTxBytes = TrafficStats.getUidTxBytes(uid);
            if ( uidRxBytes == 0 && uidTxBytes == 0 ) {
                continue;
            }
            long currentTimeMillis = System.currentTimeMillis();
            String name = app.processName;
            dao.addData(name, TimeUtils.formatTime(currentTimeMillis) + "", uidRxBytes + "", uidTxBytes + "");
        }

    }
}
