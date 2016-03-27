package com.lijingbo.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.lijingbo.mobilesafe.bean.AppInfo;
import com.lijingbo.mobilesafe.db.dao.LockPackagenameDao;

import java.util.ArrayList;
import java.util.List;

/**
 * @FileName: com.lijingbo.mobilesafe.engine.AppInfoProvider.java
 * @Author: Li Jingbo
 * @Date: 2016-02-22 21:26
 * @Version V1.0 <描述当前版本功能>
 */
public class AppInfoProvider {

    public static List< AppInfo > getAppInfo(Context context) {
        LockPackagenameDao dao = new LockPackagenameDao(context);
        List< AppInfo > appInfos = new ArrayList<>();

        PackageManager pm = context.getPackageManager();

        List< PackageInfo > packageInfos = pm.getInstalledPackages(0);

        for ( PackageInfo packageInfo : packageInfos ) {
            AppInfo appInfo = new AppInfo();
            String packageName = packageInfo.packageName;
            appInfo.setPackageName(packageName);

            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            appInfo.setIcon(icon);

            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
            appInfo.setName(name);

            int flags = packageInfo.applicationInfo.flags;
            //判断是否是用户安装的应用
            if ( (flags & ApplicationInfo.FLAG_SYSTEM) == 0 ) {
                appInfo.setUserApp(true);
            } else {
                appInfo.setUserApp(false);
            }
            //判断是否安装到了内存还是SD
            if ( (flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0 ) {
                appInfo.setInRom(true);
            } else {
                appInfo.setInRom(false);
            }

            if ( dao.findPackagename(packageName) ) {
                appInfo.setIsLock(true);
            } else {
                appInfo.setIsLock(false);
            }


            appInfos.add(appInfo);
        }

        return appInfos;
    }
}
