package com.lijingbo.mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @FileName: com.lijingbo.mobilesafe.db.dao.LockPackagenameOpenHelper.java
 * @Author: Li Jingbo
 * @Date: 2016-01-27 20:48
 * @Version V1.0 实现的SQLiteOpenHelper。实现onCreate()和onUpdate()方法。
 */
public class LockPackagenameOpenHelper extends SQLiteOpenHelper {

    public LockPackagenameOpenHelper(Context context) {
        super(context, "lockpackagename.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table lockpackagename (_id integer primary key autoincrement, packagename varchar(30))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
