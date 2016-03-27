package com.lijingbo.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lijingbo.mobilesafe.bean.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @FileName: com.lijingbo.mobilesafe.db.dao.BlackNumberDao.java
 * @Author: Li Jingbo
 * @Date: 2016-01-27 20:47
 * @Version V1.0 黑名单管理
 */
public class BlackNumberDao {
    private static final String TAG = "BlackNumberDao";
    private final BlackNumberOpenHelper helper;

    public BlackNumberDao(Context context) {
        helper = new BlackNumberOpenHelper(context);
    }

    /**
     * @param number:黑名单电话号码
     * @param mode：拦截模式
     */
    public boolean add(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        long rowId = db.insert("blacknumber", null, values);
        if ( rowId != -1 ) {
            return true;
        }
        return false;
    }

    /**
     * 删除黑名单
     *
     * @param number:黑名单电话号码
     */
    public boolean delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowNumber = db.delete("blacknumber", "number=?", new String[]{number});
        if ( rowNumber != 0 ) {
            return true;
        }
        return false;
    }

    /**
     * @param number：电话号码
     * @param mode：拦截模式
     * @return True:表示修改成功
     */
    public boolean changeNumberMode(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        int rowNumber = db.update("blacknumber", values, "number=?", new String[]{number});
        if ( rowNumber != 0 ) {
            return true;
        }

        return false;
    }

    /**
     * 通过电话号码查找拦截模式
     *
     * @return
     */
    public String findMode(String number) {
        String mode = "";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
        if ( cursor.moveToNext() ) {
            mode = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return mode;
    }

    /**
     * 通过号码查询是否在黑名单
     *
     * @param number
     * @return
     */
    public boolean findNumber(String number) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", null, "number=?", new String[]{number}, null, null, null);
        if ( cursor.moveToNext() ) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查找所有的黑名单记录
     *
     * @return
     */
    public List< BlackNumberInfo > findAll() {
        List< BlackNumberInfo > blackNumberInfos = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"mode", "number"}, null, null, null, null, null);
        while ( cursor.moveToNext() ) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            String mode = cursor.getString(0);
            String number = cursor.getString(1);
            blackNumberInfo.setMode(mode);
            blackNumberInfo.setNumber(number);
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }

    /**
     * 分页查询功能
     *
     * @param pageNumber：每页显示数量
     * @param pageSize：查询的页数
     * @return: 返回分页查询的数量
     */
    public List< BlackNumberInfo > findPage(int pageNumber, int pageSize) {
        ArrayList< BlackNumberInfo > blackNumberInfos = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?", new String[]{String.valueOf(pageSize), String.valueOf(pageSize * pageNumber)});
        while ( cursor.moveToNext() ) {
            String number = cursor.getString(cursor.getColumnIndex("number"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setNumber(number);
            blackNumberInfo.setMode(mode);
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }

    /**
     * 分批查询功能
     *
     * @param startIndex:起始位置
     * @param maxCount:最大请求数
     * @return
     */

    public List< BlackNumberInfo > findPage2(int startIndex, int maxCount) {
        ArrayList< BlackNumberInfo > blackNumberInfos = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?", new String[]{String.valueOf(maxCount), String.valueOf(startIndex)});
        while ( cursor.moveToNext() ) {
            String number = cursor.getString(cursor.getColumnIndex("number"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setNumber(number);
            blackNumberInfo.setMode(mode);
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }

    public int getCount() {
        int count = -1;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
        if ( cursor.moveToNext() ) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

}
