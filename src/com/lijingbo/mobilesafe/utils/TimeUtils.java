package com.lijingbo.mobilesafe.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @FileName: com.lijingbo.mobilesafe.utils.TimeUtils.java
 * @Author: Li Jingbo
 * @Date: 2016-03-07 19:48
 * @Version V1.0 把long类型的时间格式化为yyyy-MM-dd kk:mm:ss
 */
public class TimeUtils {
    private static final String TAG = "TimeUtils";

    public static String formatTime(long milliseconds) {
        Date date = new Date(milliseconds);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}
