package com.lijingbo.mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @FileName: com.lijingbo.mobilesafe.db.dao.BlackNumberOpenHelper.java
 * @Author: Li Jingbo
 * @Date: 2016-01-27 20:48
 * @Version V1.0 实现的SQLiteOpenHelper。实现onCreate()和onUpdate()方法。
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "BlackNumberOpenHelper";

    public BlackNumberOpenHelper(Context context) {
        super(context, "safe.db", null, 1);
    }


    /**
     * blacknumber表明
     * _id 主键  自增长
     * number  电话
     * mode    拦截模式   1.电话拦截   2.短信拦截   3.全部拦截
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber (_id integer primary key autoincrement, number varchar(20), mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
