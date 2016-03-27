package com.lijingbo.mobilesafe.utils;

import android.util.Log;

/**
 * @FileName: com.lijingbo.mobilesafe.utils.LogUtils.java
 * @Author: Li Jingbo
 * @Date: 2016-03-18 12:49
 * @Version V1.0
 */
public class LogUtils {
    private static int LEVER = 6;
    private static final int VERBOSE = 5;
    private static final int DEBUG = 4;
    private static final int INFO = 3;
    private static final int WARN = 2;
    private static final int ERROR = 1;

    public static void v(String TAG, String msg) {
        if ( LEVER >= VERBOSE ) {
            Log.v(TAG, msg);
        }
    }

    public static void d(String TAG, String msg) {
        if ( LEVER >= DEBUG ) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String TAG, String msg) {
        if ( LEVER >= INFO ) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String TAG, String msg) {
        if ( LEVER >= WARN ) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String TAG, String msg) {
        if ( LEVER >= ERROR ) {
            Log.e(TAG, msg);
        }
    }


}
