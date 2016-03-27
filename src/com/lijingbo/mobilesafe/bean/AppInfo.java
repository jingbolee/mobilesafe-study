package com.lijingbo.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * @FileName: com.lijingbo.mobilesafe.bean.AppInfo.java
 * @Author: Li Jingbo
 * @Date: 2016-02-22 21:18
 * @Version V1.0 <描述当前版本功能>
 */
public class AppInfo {
    private static final String TAG = "AppInfo";

    private Drawable icon;
    private String name;
    private String packageName;
    private boolean inRom;
    private boolean userApp;
    private boolean isLock;

    public boolean isLock() {
        return isLock;
    }

    public void setIsLock(boolean isLock) {
        this.isLock = isLock;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isInRom() {
        return inRom;
    }

    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", inRom=" + inRom +
                ", userApp=" + userApp +
                '}';
    }
}
