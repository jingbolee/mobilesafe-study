package com.lijingbo.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.lijingbo.mobilesafe.service.UpdateWidgetService;

public class MyWidget extends AppWidgetProvider {

    //当把桌面上的Widget全部都删掉的时候，调用该方法
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent stopUpdateIntent = new Intent(context, UpdateWidgetService.class);
        context.stopService(stopUpdateIntent);
    }

    //当Widget第一次创建的时候，该方法调用，然后启动后台的服务
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent startUpdateIntent = new Intent(context, UpdateWidgetService.class);
        context.startService(startUpdateIntent);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    //在Widget使用中，会多次调用该方法
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Intent startUpdateIntent = new Intent(context, UpdateWidgetService.class);
        context.startService(startUpdateIntent);
    }
}
