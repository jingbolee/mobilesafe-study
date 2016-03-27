package com.lijingbo.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @FileName: com.lijingbo.mobilesafe.engine.TaskInfoProvider.java
 * @Author: Li Jingbo
 * @Date: 2016-03-03 20:48
 * @Version V1.0 获取所有运行的进程信息，包括图标，包名，名称，内存，是否是用户应用
 */
public class TaskInfoProvider {
    private static final String TAG = "TaskInfoProvider";

    public static List< TaskInfo > getTaskInfoLists(Context context) {
        List< TaskInfo > taskInfos = new ArrayList<>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List< ActivityManager.RunningAppProcessInfo > runningAppProcessInfos = am.getRunningAppProcesses();
        for ( ActivityManager.RunningAppProcessInfo runningApp : runningAppProcessInfos ) {
            TaskInfo taskInfo = new TaskInfo();
            String packageName = runningApp.processName;
            taskInfo.setPackageName(packageName);
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
                Drawable icon = applicationInfo.loadIcon(pm);
                String name = applicationInfo.loadLabel(pm).toString();
                taskInfo.setIcon(icon);
                taskInfo.setName(name);
                if ( (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 ) {
                    taskInfo.setUserApp(true);
                } else {
                    taskInfo.setUserApp(false);
                }
            } catch ( PackageManager.NameNotFoundException e ) {
                e.printStackTrace();
                //有些程序没有名称和图标，需要设置为默认的图标和包名
                taskInfo.setIcon((context.getResources().getDrawable(R.drawable.ic_default)));
                taskInfo.setName(packageName);
            }
            Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{runningApp.pid});
            long memSize = memoryInfo[0].getTotalPrivateDirty() * 1024;
            taskInfo.setMemSize(memSize);
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
