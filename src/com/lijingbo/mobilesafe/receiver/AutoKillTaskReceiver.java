package com.lijingbo.mobilesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Debug;

import com.lijingbo.mobilesafe.utils.TimeUtils;
import com.lijingbo.mobilesafe.utils.ToastUtils;

import java.util.List;

public class AutoKillTaskReceiver extends BroadcastReceiver {

    private SharedPreferences mPre;

    @Override
    public void onReceive(Context context, Intent intent) {
        mPre = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List< ActivityManager.RunningAppProcessInfo > runningAppProcesses = am.getRunningAppProcesses();
        int killTaskNumber = 0;
        int saveMem = 0;
        for ( ActivityManager.RunningAppProcessInfo app : runningAppProcesses ) {
            if ( context.getPackageName().equals(app.processName) ) {
                continue;
            }
            am.killBackgroundProcesses(app.processName);
            killTaskNumber += 1;
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{app.pid});
            saveMem += processMemoryInfo[0].getTotalPrivateDirty() / 1024;
        }
        mPre.edit().putString("killtasktime", TimeUtils.formatTime(System.currentTimeMillis())).commit();
        mPre.edit().putInt("killTaskNumber", killTaskNumber).commit();
        mPre.edit().putInt("saveMem", saveMem).commit();
        ToastUtils.showShortToast(context, "共清理了" + killTaskNumber + "个进程，释放了" + saveMem + "MB内存");
//        System.out.println("接受到了广播，并杀掉了后台进程");
    }
}
