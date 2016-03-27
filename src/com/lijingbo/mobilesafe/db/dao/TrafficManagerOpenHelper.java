package com.lijingbo.mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @FileName: com.lijingbo.mobilesafe.db.dao.TrafficManagerOpenHelper.java
 * @Author: Li Jingbo
 * @Date: 2016-01-27 20:48
 * @Version V1.0 实现的SQLiteOpenHelper。实现onCreate()和onUpdate()方法。
 */
public class TrafficManagerOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "TrafficManagerOpenHelper";

    public TrafficManagerOpenHelper(Context context) {
        super(context, "traffic.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table traffic (_id integer primary key autoincrement, packname varchar(20), date varchar(20), senddata varchar(20), recdata varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
