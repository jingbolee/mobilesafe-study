package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.service.AutoKillTaskService;
import com.lijingbo.mobilesafe.utils.SystemInfoUtils;
import com.lijingbo.mobilesafe.utils.TimeUtils;
import com.lijingbo.mobilesafe.view.SettingItemView;

public class TaskSettingActivity extends Activity {
    private SettingItemView siv_autokillprocess;
    private RelativeLayout rl_tasksystemvisible;
    private CheckBox cb_tasksystemvisible;
    private SharedPreferences mPre;
    private boolean tasksystemvisible;
    private String killtasktime;
    private int killTaskNumber;
    private int saveMem;
    private TextView tv_showtime;
    private TextView tv_showkillinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPre = getSharedPreferences("config", MODE_PRIVATE);

        setContentView(R.layout.activity_task_setting);
        cb_tasksystemvisible = (CheckBox) findViewById(R.id.cb_tasksystemvisible);
        rl_tasksystemvisible = (RelativeLayout) findViewById(R.id.rl_tasksystemvisible);
        tv_showtime = (TextView) findViewById(R.id.tv_showtime);
        tv_showkillinfo = (TextView) findViewById(R.id.tv_showkillinfo);


        killTaskNumber = mPre.getInt("killTaskNumber", 0);
        saveMem = mPre.getInt("saveMem", 0);
        if ( mPre.getBoolean("autokillprocess", false) ) {
            if ( saveMem != 0 && killTaskNumber != 0 ) {
                killtasktime = mPre.getString("killtasktime", TimeUtils.formatTime(System.currentTimeMillis()));
                tv_showtime.setText("上次清理时间：" + killtasktime);
                tv_showkillinfo.setText("共清理了" + killTaskNumber + "个进程，释放了" + saveMem + "MB内存");
            }
        } else {
            tv_showtime.setText("没有开启定时清理进程功能");
            tv_showkillinfo.setVisibility(View.INVISIBLE);
        }
//        if ( saveMem == 0 || killTaskNumber == 0 ) {
//            tv_showkillinfo.setVisibility(View.INVISIBLE);
//        } else {
//            tv_showkillinfo.setText("共清理了" + killTaskNumber + "个进程，释放了" + saveMem + "MB内存");
//
//        }

        initAutoKillProcess();//初始化自动清理进程

        tasksystemvisible = mPre.getBoolean("tasksystemvisible", false);

        if ( tasksystemvisible ) {
            cb_tasksystemvisible.setChecked(true);
        } else {
            cb_tasksystemvisible.setChecked(false);
        }

        rl_tasksystemvisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( cb_tasksystemvisible.isChecked() ) {
                    cb_tasksystemvisible.setChecked(false);
                    mPre.edit().putBoolean("tasksystemvisible", false).commit();
                    tasksystemvisible = false;
                } else {
                    cb_tasksystemvisible.setChecked(true);
                    mPre.edit().putBoolean("tasksystemvisible", true).commit();
                    tasksystemvisible = true;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("tasksystemvisible", tasksystemvisible);
//        System.out.println("传递给TaskManagerActivity的值为：" + tasksystemvisible);
        setResult(RESULT_OK, intent);

        super.onBackPressed();

    }


    private void initAutoKillProcess() {
        siv_autokillprocess = (SettingItemView) findViewById(R.id.siv_autokillprocess);
        boolean autokillprocess = mPre.getBoolean("autokillprocess", false);
        final Intent intent = new Intent(TaskSettingActivity.this, AutoKillTaskService.class);
        if ( autokillprocess ) {
            siv_autokillprocess.setChecked(true);
            startService(intent);
        } else {
            siv_autokillprocess.setChecked(false);
            if ( SystemInfoUtils.isServiceRunning(TaskSettingActivity.this, "com.lijingbo.mobilesafe.service.AutoKillTaskService") ) {
                stopService(intent);
            }
        }
        siv_autokillprocess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( siv_autokillprocess.isChecked() ) {
                    siv_autokillprocess.setChecked(false);
                    mPre.edit().putBoolean("autokillprocess", false).commit();
                    stopService(intent);
                } else {
                    siv_autokillprocess.setChecked(true);
                    mPre.edit().putBoolean("autokillprocess", true).commit();
                    startService(intent);
                }
            }
        });
    }

}
