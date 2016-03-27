package com.lijingbo.mobilesafe.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.lijingbo.mobilesafe.db.dao.BlackNumberDao;
import com.lijingbo.mobilesafe.receiver.BlackNumberReceiver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CallSafeService extends Service {

    private BlackNumberReceiver blackNumberReceiver;
    private TelephonyManager tm;
    private BlackNumberDao dao;
    private BlackNumberLisenter blackNumberLisenter;

    public CallSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        blackNumberReceiver = new BlackNumberReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(blackNumberReceiver, intentFilter);
        dao = new BlackNumberDao(this);
        //获取到TelephonyManager对象
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        blackNumberLisenter = new BlackNumberLisenter();
        //监听来电状态
        tm.listen(blackNumberLisenter, PhoneStateListener.LISTEN_CALL_STATE);
    }

    class BlackNumberLisenter extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch ( state ) {
                //铃声响起的时候
                case TelephonyManager.CALL_STATE_RINGING:
                    String mode = dao.findMode(incomingNumber);
//                    System.out.println("来电号码：" + incomingNumber);
                    if ( mode.equals("1") || mode.equals("3") ) {

                        //需要使用内容观察者进行删除挂断电话记录
                        Uri uri = Uri.parse("content://call_log/calls");
                        CallLogsContentObserver callLogsContentObserver = new CallLogsContentObserver(incomingNumber, new Handler());
                        getContentResolver().registerContentObserver(uri, true, callLogsContentObserver);
                        //挂断电话
                        endCall();
                    }
                    break;
            }
        }
    }

    /**
     * 内容观察者，查看到calls表中存在通话记录，立马删除
     */
    class CallLogsContentObserver extends ContentObserver {


        private String incomingNumber;

        public CallLogsContentObserver(String incomingNumber, Handler handler) {

            super(handler);
            this.incomingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber);
        }
    }

    /**
     * 通过ContentProvider，删除被挂断电话号码的通话记录
     *
     * @param incomingNumber:要删除的电话号码
     */
    private void deleteCallLog(String incomingNumber) {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");
        contentResolver.delete(uri, "number=?", new String[]{incomingNumber});
    }

    private void endCall() {
        /**该方法使用反射，获取到SystemManger的方法getService()
         * SystemManger隐藏了，无法直接使用。需要使用反射
         *IBinder iBinder=SystemManger.getService(TELEPHONY_SERVICE);
         */
        try {
            //获取到systemmanger的字节码
            Class< ? > clazz = CallSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
            //获取到该方法，参数1：方法名，参数2：该方法需要传入参数的类型
            Method method = clazz.getDeclaredMethod("getService", String.class);
            try {
                //使用该方法，参数1：该方法的执行者，static可以设置为null    参数2：该方法的参数
                IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
                //通过iBinder获取到ITelephony对象，然后就可以调用ITelephony的endCall()方法了。
                ITelephony iTelephony = ITelephony.Stub.asInterface(ibinder);
                try {
                    //挂断电话
                    iTelephony.endCall();
                } catch ( RemoteException e ) {
                    e.printStackTrace();
                }
            } catch ( IllegalAccessException e ) {
                e.printStackTrace();
            } catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //service销毁时，注销广播，并把广播接收者设为null

        if ( blackNumberReceiver != null ) {
            unregisterReceiver(blackNumberReceiver);
            blackNumberReceiver = null;
        }
        //service销毁时，把电话监听设置为none，并把监听器设为null
        if ( blackNumberLisenter != null ) {
            tm.listen(blackNumberLisenter, PhoneStateListener.LISTEN_NONE);
            blackNumberLisenter = null;
        }
    }
}
