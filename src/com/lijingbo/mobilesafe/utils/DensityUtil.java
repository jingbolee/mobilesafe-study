package com.lijingbo.mobilesafe.utils;

import android.content.Context;

/**
 * @FileName: com.lijingbo.mobilesafe.utils.DensityUtil.java
 * @Author: Li Jingbo
 * @Date: 2016-02-25 15:33
 * @Version V1.0 dp和px之间转换工具
 */
public class DensityUtil {

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
