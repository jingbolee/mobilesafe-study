package com.lijingbo.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @FileName: com.lijingbo.mobilesafe.utils.SmsUtils.java
 * @Author: Li Jingbo
 * @Date: 2016-02-02 10:06
 * @Version V1.0 短信工具类，实现短信的备份和还原功能
 */
public class SmsUtils {

    private static final String TAG = "SmsUtils";

    /**
     * 抽取成接口，降低耦合。
     * 把经常需要改变的部分抽取成接口，然后使用回调函数
     */
    public interface BackUpCallBack {
        void beforeSmsBackUp(int max);

        void onSmsBackUp(int progress);

    }

    /**
     * 短信备份
     */
    public static void backUpSms(Context context, BackUpCallBack callBack) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
        FileOutputStream fos = new FileOutputStream(file);
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(fos, "utf-8");
        serializer.startDocument("utf-8", true);
        serializer.startTag(null, "message");

        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "read", "body"}, null, null, null);
        int count = cursor.getCount();
//        pd.beforeSmsBackUp(count);
        callBack.beforeSmsBackUp(count);
        serializer.attribute(null, "max", count + "");
        int progress = 0;
        while ( cursor.moveToNext() ) {
//            SystemClock.sleep(100);
            String address = cursor.getString(0);
            String date = cursor.getString(1);
            String type = cursor.getString(2);
            String read = cursor.getString(3);
            String body = cursor.getString(4);

            serializer.startTag(null, "sms");
            serializer.startTag(null, "address");
            serializer.text(address);
            serializer.endTag(null, "address");

            serializer.startTag(null, "date");
            serializer.text(date);
            serializer.endTag(null, "date");

            serializer.startTag(null, "type");
            serializer.text(type);
            serializer.endTag(null, "type");

            serializer.startTag(null, "read");
            serializer.text(read);
            serializer.endTag(null, "read");

            serializer.startTag(null, "body");
            serializer.text(body);
            serializer.endTag(null, "body");
            serializer.endTag(null, "sms");
            progress++;
//            pd.onSmsBackUp(progress);
            callBack.onSmsBackUp(progress);
        }
        serializer.endTag(null, "message");
        serializer.endDocument();
        fos.close();
    }

    public interface RestoreCallBack {

        void beforeSmsBackUp(int max);

        void onSmsBackUp(int progress);
    }

    /**
     * 短信还原
     */
    public static void restoreSms(Context context, boolean delPhoneSms, RestoreCallBack callBack) throws IOException, XmlPullParserException {
        File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
        FileInputStream fis = new FileInputStream(file);
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        //根据用户选择，是否删除手机中现有的短信
        if ( delPhoneSms ) {
            resolver.delete(uri, null, null);
        }
        ContentValues values = new ContentValues();
        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(fis, "utf-8");
        int progress = 0;
        int eventType = xpp.getEventType();
        while ( eventType != XmlPullParser.END_DOCUMENT ) {
            switch ( eventType ) {
                case XmlPullParser.START_TAG:
//                    SystemClock.sleep(100);
                    if ( "message".equals(xpp.getName()) ) {
                        String max = xpp.getAttributeValue(null, "max");
                        callBack.beforeSmsBackUp(Integer.valueOf(max));
                    } else if ( "sms".equals(xpp.getName()) ) {

                    } else if ( "body".equals(xpp.getName()) ) {
                        String body = xpp.nextText();
                        values.put("body", body);
                    } else if ( "date".equals(xpp.getName()) ) {
                        String date = xpp.nextText();
                        values.put("date", date);
                    } else if ( "type".equals(xpp.getName()) ) {
                        String type = xpp.nextText();
                        values.put("type", type);
                    } else if ( "read".equals(xpp.getName()) ) {
                        String read = xpp.nextText();
                        values.put("read", read);
                    } else if ( "address".equals(xpp.getName()) ) {
                        String address = xpp.nextText();
                        values.put("address", address);
                    }

                    break;
                case XmlPullParser.END_TAG:
                    if ( "sms".equals(xpp.getName()) ) {
                        resolver.insert(uri, values);
                        values.clear();
                        progress++;
                        callBack.onSmsBackUp(progress);
                    }
                    break;
                default:
                    break;

            }
            eventType = xpp.next();
        }


    }
}
