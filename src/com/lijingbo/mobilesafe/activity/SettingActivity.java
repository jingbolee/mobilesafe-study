package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.service.AddressService;
import com.lijingbo.mobilesafe.service.CallSafeService;
import com.lijingbo.mobilesafe.service.WatchDogService;
import com.lijingbo.mobilesafe.utils.SystemInfoUtils;
import com.lijingbo.mobilesafe.view.SettingClickView;
import com.lijingbo.mobilesafe.view.SettingItemView;

public class SettingActivity extends Activity {

    private SettingItemView sivUpdate;
    private SettingItemView sivAddress;
    private SharedPreferences mPre;
    private SettingClickView scvAddressStyle;
    private SettingClickView scvAddressLocation;
    private SettingItemView siv_call_safe;
    private SettingItemView siv_lock_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initAddressView();    //设置归属地查询
        initUpdateView();    //设置升级
        initAddressStyleView();  //设置归属地风格
        initAddressLocation();   //设置归属地提示框位置
        initCallSafeView();   //设置归属地提示框位置
        initLockAppView();     //设置开门狗

    }

    //设置开门狗
    private void initLockAppView() {
        siv_lock_app = (SettingItemView) findViewById(R.id.siv_lock_app);
        boolean serviceRunning = SystemInfoUtils.isServiceRunning(this, "com.lijingbo.mobilesafe.service.WatchDogService");
        if ( serviceRunning ) {
            siv_lock_app.setChecked(true);
        } else {
            siv_lock_app.setChecked(false);
        }
        siv_lock_app.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( siv_lock_app.isChecked() ) {
                    siv_lock_app.setChecked(false);
                    stopService(new Intent(SettingActivity.this, WatchDogService.class));
                } else {
                    siv_lock_app.setChecked(true);
                    startService(new Intent(SettingActivity.this, WatchDogService.class));
                }
            }
        });
    }

    /**
     * 初始化黑名单拦截功能
     */
    private void initCallSafeView() {
        siv_call_safe = (SettingItemView) findViewById(R.id.siv_call_safe);
        boolean result = SystemInfoUtils.isServiceRunning(this, "com.lijingbo.mobilesafe.service.CallSafeService");
        if ( result ) {
            siv_call_safe.setChecked(true);
        } else {
            siv_call_safe.setChecked(false);
        }
        siv_call_safe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( siv_call_safe.isChecked() ) {
                    siv_call_safe.setChecked(false);
                    stopService(new Intent(SettingActivity.this,
                            CallSafeService.class));
                } else {
                    siv_call_safe.setChecked(true);
                    startService(new Intent(SettingActivity.this,
                            CallSafeService.class));
                }
            }
        });
    }


    /**
     * 初始化自动更新开关
     */
    private void initUpdateView() {
        sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
        mPre = getSharedPreferences("config", MODE_PRIVATE);
        // 自动更新设置为开启
        boolean autoUpdate = mPre.getBoolean("auto_update", true);
        if ( autoUpdate ) {
            sivUpdate.setChecked(true);
        } else {
            sivUpdate.setChecked(false);
        }

        sivUpdate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if ( sivUpdate.isChecked() ) {
                    sivUpdate.setChecked(false);
                    mPre.edit().putBoolean("auto_update", false).commit();
                } else {
                    sivUpdate.setChecked(true);
                    mPre.edit().putBoolean("auto_update", true).commit();
                }
            }
        });
    }

    /**
     * 初始化来电归属地开关
     */
    private void initAddressView() {
        sivAddress = (SettingItemView) findViewById(R.id.siv_address);
        // 判断归属地service是否启动，没有启动的话，去掉勾选框。
        boolean serviceRunning = SystemInfoUtils.isServiceRunning(this,
                "com.lijingbo.mobilesafe.service.AddressService");
        if ( serviceRunning ) {
            sivAddress.setChecked(true);
        } else {
            sivAddress.setChecked(false);
        }
        sivAddress.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if ( sivAddress.isChecked() ) {
                    sivAddress.setChecked(false);
                    stopService(new Intent(SettingActivity.this,
                            AddressService.class));
                } else {
                    sivAddress.setChecked(true);
                    startService(new Intent(SettingActivity.this,
                            AddressService.class));
                }
            }
        });
    }

    private void initAddressStyleView() {
        final String[] items = new String[]{"半透明", "活力橙", "天空蓝", "金属灰", "苹果绿"};
        scvAddressStyle = (SettingClickView) findViewById(R.id.scv_address_style);
//		scvAddressStyle.setTitle("归属地设置风格");
        int style = mPre.getInt("address_style", 0);
        scvAddressStyle.setDesc(items[style]);
        scvAddressStyle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        SettingActivity.this);
                builder.setTitle("归属地提示框风格");
                int style = mPre.getInt("address_style", 0);
                builder.setSingleChoiceItems(items, style,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mPre.edit().putInt("address_style", which)
                                        .commit();
                                scvAddressStyle.setDesc(items[which]);
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });
    }

    /*
     * 初始化归属地提示框位置
     */
    private void initAddressLocation() {
        scvAddressLocation = (SettingClickView) findViewById(R.id.scv_address_location);
        scvAddressLocation.setDesc("设置归属地提示框的显示位置");
        scvAddressLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, DragViewActivity.class));
            }
        });
    }

}
