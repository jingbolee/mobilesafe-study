package com.lijingbo.mobilesafe.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;


public class SystemInfoUtils {

    // 判断service是否在运行
    public static boolean isServiceRunning(Context ctx, String serviceName) {
        ActivityManager am = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        List< RunningServiceInfo > runningServices = am.getRunningServices(200); // 获取运行的所有service，100一般可以获取到所有的service.
        for ( RunningServiceInfo runningServiceInfo : runningServices ) {
            String className = runningServiceInfo.service.getClassName();    //获取到运行服务的类名
            if ( className.equals(serviceName) ) { // 假如在运行的service中含有传入的service名称，返回true
                return true;
            }
        }
        return false;
    }

    //获取设备中运行的进程数
    public static int getRunningTaskCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List< ActivityManager.RunningAppProcessInfo > runningAppProcesses = am.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    //Android API获取设备可用内存
    public static long getAvaliMem(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    //通过IO流，读取到系统的meminfo文件，然后获取到内存信息
    public static long getTotalMem2(Context context) {
        File file = new File("/proc/meminfo");
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();
            StringBuilder builder = new StringBuilder();
            for ( char c : line.toCharArray() ) {
                if ( c >= '0' || c <= '9' ) {
                    builder.append(c);
                }
            }
            return Long.parseLong(builder.toString()) * 1024;
        } catch ( Exception e ) {
            e.printStackTrace();
            return 0;
        }

    }

    //通过Android API获取系统总内存
    public static long getTotalMem(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.totalMem;
    }


}
