package com.lijingbo.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * @FileName: com.lijingbo.mobilesafe.bean.TrafficInfoBean.java
 * @Author: Li Jingbo
 * @Date: 2016-03-17 15:20
 * @Version V1.0 <描述当前版本功能>
 */
public class TrafficInfoBean {
    private static final String TAG = "TrafficInfoBean";

    private String packname;
    private String name;
    private String date;
    private String senddata;
    private String recdata;
    private Drawable icon;
    private boolean userApp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSenddata() {
        return senddata;
    }

    public void setSenddata(String senddata) {
        this.senddata = senddata;
    }

    public String getRecdata() {
        return recdata;
    }

    public void setRecdata(String recdata) {
        this.recdata = recdata;
    }

    @Override
    public String toString() {
        return "TrafficInfoBean{" +
                "packname='" + packname + '\'' +
                ", date='" + date + '\'' +
                ", senddata='" + senddata + '\'' +
                ", recdata='" + recdata + '\'' +
                '}';
    }
}
