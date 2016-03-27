package com.lijingbo.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.db.dao.AddressDao;
import com.lijingbo.mobilesafe.utils.LogUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 来电（收到电话）使用service监听，去电（拨打电话）使用注册动态广播监听
 *
 * @author Jerome Li ,Jan 20, 2016 4:23:35 PM
 */
public class AddressService extends Service {
    private static final String TAG = "AddressService";
    private TelephonyManager tm;
    private MyTelephonyListener listener;
    private OutCallReceiver receiver;
    private WindowManager mWm;
    private View view;
    private SharedPreferences mPref;
    private WindowManager.LayoutParams params;

    private int startX;
    private int startY;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        // 监听来电
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyTelephonyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE); // 监听来电状态

        receiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter); // 动态注册广播

    }

    class MyTelephonyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch ( state ) {
                case TelephonyManager.CALL_STATE_RINGING:
                    String address = AddressDao.getAddress(incomingNumber); // 通过电话号码，查询归属地
                    showToast(address);
                    //十秒以后，移出浮窗
                    delayDismissToast();
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    dismissToast();
                default:
                    break;
            }
        }
    }

    // 去电使用广播 需要申请权限：android.permission.PROCESS_OUTGOING_CALLS
    class OutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();
            LogUtils.e(TAG, "去电的号码：" + number);
            if ( !TextUtils.isEmpty(number) ) {
                String address = AddressDao.getAddress(number);
                showToast(address);
                //倒计时10秒以后移出浮窗
                delayDismissToast();
            }
        }

    }

    private void delayDismissToast() {
        //15秒以后，关闭浮窗
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                dismissToast();
            }
        };
        long delay = 10 * 1000;
        timer.schedule(task, delay);
    }

    class CloseView extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.e(TAG, "关闭浮窗了");
            dismissToast();
        }
    }

    @Override
    public void onDestroy() {
        if ( listener != null ) {
            tm.listen(listener, PhoneStateListener.LISTEN_NONE); // 停止来电服务监听
            listener = null;
        }
        if ( receiver != null ) {
            unregisterReceiver(receiver); // 停止去电广播监听
            receiver = null;
        }
        super.onDestroy();

    }

    // 自定义归属地显示浮窗
    private void showToast(String text) {
        mWm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final int screenHeight = mWm.getDefaultDisplay().getHeight();
        final int screenWidth = mWm.getDefaultDisplay().getWidth();
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE; // 修改view显示的类型为TYPE_PHONE，级别比Toast高,需要申请权限

        params.gravity = Gravity.LEFT + Gravity.TOP;// 设置屏幕左上角为起点，系统默认为屏幕正中间
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON // 去掉了不可触控的flag
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        int lastTop = mPref.getInt("lastTop", 0);
        int lastLeft = mPref.getInt("lastLeft", 0);

        // 设置控件的绘制坐标点
        params.x = lastLeft;
        params.y = lastTop;

        view = View.inflate(AddressService.this, R.layout.toast_address_view,
                null);
        int[] styles = new int[]{R.drawable.call_locate_white,
                R.drawable.call_locate_orange, R.drawable.call_locate_blue,
                R.drawable.call_locate_gray, R.drawable.call_locate_green};
        int style = mPref.getInt("address_style", 0);
        view.setBackgroundResource(styles[style]); // 根据用户选择的归属地背景颜色，设置背景色
        TextView tvNumber = (TextView) view.findViewById(R.id.tv_numer);
        tvNumber.setText(text);

        mWm.addView(view, params);

        view.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch ( event.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();
//					System.out.println("X:"+endX+";y:"+endY);
                        // 计算偏移量
                        int dX = endX - startX;
                        int dY = endY - startY;

                        params.x = params.x + dX;
                        params.y = params.y + dY;

                        //防止view的位置跑出屏幕
                        if ( params.x < 0 ) {
                            params.x = 0;
                        }
                        if ( params.y < 0 ) {
                            params.y = 0;
                        }
                        if ( params.x > screenWidth - view.getWidth() ) {
                            params.x = screenWidth - view.getWidth();
                        }
                        if ( params.y > screenHeight - view.getHeight() ) {
                            params.y = screenHeight - view.getHeight();
                        }
                        // 更新布局
                        mWm.updateViewLayout(view, params);

                        // 重新获取x和y的值
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        mPref.edit().putInt("lastTop", params.y)
                                .commit();
                        mPref.edit().putInt("lastLeft", params.x)
                                .commit();
                        break;

                    default:
                        break;
                }

                return true;
            }
        });
    }


    //移出浮窗
    private void dismissToast() {
        if ( mWm != null && view != null ) {
            mWm.removeView(view); // 去掉浮窗
            view = null;
        }

    }
}
