package com.lijingbo.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.lijingbo.mobilesafe.service.TrafficListenService;

/*
 * 开机启动，检测sim是否一致
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences mPref = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		String savedSim = mPref.getString("sim", "");
		boolean protect=mPref.getBoolean("protect", false);
		if (protect) {
			if (!TextUtils.isEmpty(savedSim)) {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				String currentSim = tm.getSimSerialNumber();
				if (!savedSim.equals(currentSim)) {
//					System.out.println("手机SIM卡已更换");
					String savedSafeNumber=mPref.getString("safeNumber", "");
					SmsManager.getDefault().sendTextMessage(savedSafeNumber, null, "Sim card changed!", null, null);
				} else {
//					System.out.println("手机安全");
				}
			}
		}
        //开机以后，启动流量统计服务
        context.startService(new Intent(context, TrafficListenService.class));
    }

}
