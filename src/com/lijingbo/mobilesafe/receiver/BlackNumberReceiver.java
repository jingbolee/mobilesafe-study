package com.lijingbo.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.lijingbo.mobilesafe.db.dao.BlackNumberDao;

public class BlackNumberReceiver extends BroadcastReceiver {
    public BlackNumberReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] object = (Object[]) intent.getExtras().get("pdus");
        for ( Object ob : object ) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) ob,
                    "3gpp");
            String originatingAddress = smsMessage.getOriginatingAddress();
            String messageBody = smsMessage.getMessageBody();
//            System.out.println(originatingAddress + ":" + messageBody);
            BlackNumberDao dao = new BlackNumberDao(context);
            String mode = dao.findMode(originatingAddress);

            if ( mode.equals("1") ) {
//                System.out.println("电话拦截");
            } else if ( mode.equals("2") ) {
                abortBroadcast();
//                System.out.println("短信拦截");
            } else if ( mode.equals("3") ) {
                abortBroadcast();
//                System.out.println("电话拦截+短信");
            }
        }

    }
}
