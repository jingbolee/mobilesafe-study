package com.lijingbo.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.service.LocationService;

public class SmsMangerReceiver extends BroadcastReceiver {
    private DevicePolicyManager mDPM;
    private SharedPreferences mPref;

    @Override
    public void onReceive(Context context, Intent intent) {
        mDPM = (DevicePolicyManager) context
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        mPref = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        boolean protect = mPref.getBoolean("protect", false);
        //开启了保护以后，才会激活短信命令。
        String format = intent.getStringExtra("format");
        if ( protect ) {
            Object[] object = (Object[]) intent.getExtras().get("pdus");
            for ( Object ob : object ) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) ob,
                        format);
                String originatingAddress = smsMessage.getOriginatingAddress();
                String messageBody = smsMessage.getMessageBody();
                if ( messageBody.equals("#*alarm*#") ) {
                    // 使用MediaPlayer播放音乐
                    MediaPlayer player = MediaPlayer
                            .create(context, R.raw.ylzs);
                    player.setVolume(1.0f, 1.0f);
                    player.setLooping(true);
                    player.start();
                    abortBroadcast();
                } else if ( messageBody.equals("#*location*#") ) {
                    // 通过service获取到经纬度
                    context.startService(new Intent(context,
                            LocationService.class));
                    abortBroadcast();
                } else if ( messageBody.equals("#*wipedata*#") ) {

                    mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                    abortBroadcast();

                } else if ( messageBody.equals("#*lockscreen*#") ) {

                    mDPM.lockNow();
                    mDPM.resetPassword("123456", 0);
                    abortBroadcast();
                }
            }
        }
    }
}
