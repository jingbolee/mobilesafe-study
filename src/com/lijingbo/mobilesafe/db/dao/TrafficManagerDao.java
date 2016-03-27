package com.lijingbo.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lijingbo.mobilesafe.bean.TrafficInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @FileName: com.lijingbo.mobilesafe.db.dao.TrafficManagerDao.java
 * @Author: Li Jingbo
 * @Date: 2016-03-15 21:50
 * @Version V1.0 流量数据库的Dao
 */
public class TrafficManagerDao {
    private static final String TAG = "TrafficManagerDao";
    private TrafficManagerOpenHelper helper;

    public TrafficManagerDao(Context context) {
        helper = new TrafficManagerOpenHelper(context);
    }

    //增加发送数据
    public void addData(String name, String date, String senddata, String recdata) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packname", name);
        values.put("date", date);
        values.put("senddata", senddata);
        values.put("recdata", recdata);
        db.insert("traffic", null, values);
        db.close();
    }


    //查询数据
    public long[] queryData(String packname) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("traffic", null, "packname=?", new String[]{packname}, null, null, null);
        List< TrafficInfoBean > trafficInfos = new ArrayList<>();
        while ( cursor.moveToNext() ) {
            TrafficInfoBean bean = new TrafficInfoBean();
            bean.setPackname(cursor.getString(cursor.getColumnIndex("packname")));
            bean.setDate(cursor.getString(cursor.getColumnIndex("date")));
            bean.setRecdata(cursor.getString(cursor.getColumnIndex("recdata")));
            bean.setSenddata(cursor.getString(cursor.getColumnIndex("senddata")));
            trafficInfos.add(bean);
        }
        long[] traffic = parseTraffic(trafficInfos);
        cursor.close();
        db.close();
        return traffic;

    }

    //解析从数据库查询出来的集合中应用记录的所有的流量信息
    private long[] parseTraffic(List< TrafficInfoBean > trafficInfos) {
        long recdata = 0;
        long senddata = 0;
        for ( int i = 0; i < trafficInfos.size() - 1; i++ ) {
            long[] compareData = compareData(trafficInfos.get(i), trafficInfos.get(i + 1));
            if ( compareData[0] >= recdata ) {
                recdata = compareData[0];
            } else {
                recdata = compareData[0] + recdata;
            }
            if ( compareData[1] >= senddata ) {
                senddata = compareData[1];
            } else {
                senddata = compareData[1] + senddata;
            }
        }
        return new long[]{recdata, senddata};
    }

    private long[] compareData(TrafficInfoBean bean1, TrafficInfoBean bean2) {
        long recdata;
        long senddata;
        String recdata1 = bean1.getRecdata();
        String recdata2 = bean2.getRecdata();
        String senddata1 = bean1.getSenddata();
        String senddata2 = bean2.getSenddata();
        long rec1 = Long.parseLong(recdata1);
        long rec2 = Long.parseLong(recdata2);
        long send1 = Long.parseLong(senddata1);
        long send2 = Long.parseLong(senddata2);
        if ( (rec2 - rec1) >= 0 ) {
            recdata = rec2;
        } else {
            recdata = rec2 + rec1;
        }

        if ( (send2 - send1) >= 0 ) {
            senddata = send2;
        } else {
            senddata = send2 + send1;
        }
        return new long[]{recdata, senddata};
    }


}
