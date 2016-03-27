package com.lijingbo.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * @FileName: com.lijingbo.mobilesafe.bean.TaskInfo.java
 * @Author: Li Jingbo
 * @Date: 2016-03-03 20:51
 * @Version V1.0 进程信息的bean
 */
public class TaskInfo {
    private static final String TAG = "TaskInfo";


    private Drawable icon;
    private String name;
    private long memSize;
    private boolean userApp;
    private String packageName;
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
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

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "name='" + name + '\'' +
                ", memSize=" + memSize +
                ", userApp=" + userApp +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
