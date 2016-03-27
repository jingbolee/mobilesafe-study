package com.lijingbo.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * @FileName: com.lijingbo.mobilesafe.db.dao.LockPackagenameDao.java
 * @Author: Li Jingbo
 * @Date: 2016-03-12 12:15
 * @Version V1.0 <描述当前版本功能>
 */
public class LockPackagenameDao {

    private final LockPackagenameOpenHelper helper;

    public LockPackagenameDao(Context context) {
        helper = new LockPackagenameOpenHelper(context);
    }

    public void addPackagename(String packagename) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename", packagename);
        db.insert("lockpackagename", null, values);
        db.close();
    }

    public void deletePackagename(String packagename) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("lockpackagename", "packagename=?", new String[]{packagename});
        db.close();
    }

    public boolean findPackagename(String packagename) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("lockpackagename", null, "packagename=?", new String[]{packagename}, null, null, null);
        if ( cursor.moveToNext() ) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    public List< String > findAll() {
        List< String > appInfolist = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("lockpackagename", null, null, null, null, null, null);
        while ( cursor.moveToNext() ) {
            appInfolist.add(cursor.getString(1));
        }
        cursor.close();
        db.close();
        return appInfolist;
    }


}
