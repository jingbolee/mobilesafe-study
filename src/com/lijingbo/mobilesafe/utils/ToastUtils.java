package com.lijingbo.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	
	public static void showShortToast(Context ctx,String text){
		Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
	}
	
	
	public static void showLongToast(Context ctx,String text){
		Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
	}
}
