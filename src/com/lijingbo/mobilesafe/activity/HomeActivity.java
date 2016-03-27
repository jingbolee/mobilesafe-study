package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.adapter.MyGridAdapter;
import com.lijingbo.mobilesafe.service.AutoKillTaskService;
import com.lijingbo.mobilesafe.service.TrafficListenService;
import com.lijingbo.mobilesafe.utils.MD5Utils;
import com.lijingbo.mobilesafe.utils.SystemInfoUtils;

public class HomeActivity extends Activity {
    private GridView gvHome;

    private String[] mItems = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};

    private int[] mPics = new int[]{R.drawable.home_safe,
            R.drawable.home_callmsgsafe, R.drawable.home_apps,
            R.drawable.home_taskmanager, R.drawable.home_netmanager,
            R.drawable.home_trojan, R.drawable.home_sysoptimize,
            R.drawable.home_tools, R.drawable.home_settings};

    private SharedPreferences mPref;
    private boolean autokillprocess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        //判断是否启动关屏自动杀掉进程的功能
        autokillprocess = mPref.getBoolean("autokillprocess", false);
        Intent intent = new Intent(this, AutoKillTaskService.class);
        if ( autokillprocess ) {
            startService(intent);
        } else {
            if ( SystemInfoUtils.isServiceRunning(this, "com.lijingbo.mobilesafe.service.AutoKillTaskService") ) {
                stopService(intent);
            }
        }
        startAlarmTrafficService();
        gvHome = (GridView) findViewById(R.id.gv_home);

        gvHome.setAdapter(new MyGridAdapter(HomeActivity.this, mItems, mPics));
        gvHome.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView< ? > parent, View view,
                                    int position, long id) {
                switch ( position ) {
                    case 0:
                        // 手机防盗
                        String savedPassword = mPref.getString("password", null);
                        if ( TextUtils.isEmpty(savedPassword) ) {
                            showSetPasswordDialog();
                        } else {
                            showInputPasswordDialog(savedPassword);
                        }

                        break;
                    case 1:
                        // 通讯卫士
                        startActivity(new Intent(HomeActivity.this,
                                CallSafeActivity2.class));
                        break;
                    case 2:
                        // 软件管理
                        startActivity(new Intent(HomeActivity.this,
                                AppManagerActivity.class));
                        break;
                    case 3:
                        // 进程管理
                        startActivity(new Intent(HomeActivity.this, TaskManagerActivity.class));
                        break;
                    case 4:
                        // 流量统计
                        startActivity(new Intent(HomeActivity.this, TrafficStatisticsActivity.class));
                        break;
                    case 5:
                        // 手机杀毒
                        startActivity(new Intent(HomeActivity.this, AntivirusActivity.class));
                        break;
                    case 6:
                        // 缓存清理
                        startActivity(new Intent(HomeActivity.this, CacheClearActivity.class));
                        break;
                    case 7:
                        // 高级工具
                        startActivity(new Intent(HomeActivity.this,
                                AToolsActivity.class));
                        break;
                    case 8:
                        // 设置中心
                        startActivity(new Intent(HomeActivity.this,
                                SettingActivity.class));
                        break;

                    default:
                        break;
                }
            }

        });
    }

    //启动获取数据记录的service
    private void startAlarmTrafficService() {
        Intent intent = new Intent(HomeActivity.this, TrafficListenService.class);
        startService(intent);
    }

    protected void showInputPasswordDialog(final String savedPassword) {
        View view = View.inflate(this, R.layout.dialog_input_password, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        dialog.setView(view);

        final EditText etPassword = (EditText) view
                .findViewById(R.id.et_password);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                if ( !TextUtils.isEmpty(password) ) {
                    String md5Password = MD5Utils.encode(password);
                    if ( md5Password.equals(savedPassword) ) {
                        // Toast.makeText(HomeActivity.this, "登录成功！",
                        // Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this,
                                LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "密码错误！",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    protected void showSetPasswordDialog() {
        View view = View.inflate(this, R.layout.dialog_set_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        dialog.setView(view);
        final EditText etPassword = (EditText) view
                .findViewById(R.id.et_password);
        final EditText etPasswordConfim = (EditText) view
                .findViewById(R.id.et_password_confim);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String passwordConfim = etPasswordConfim.getText().toString();
                if ( !TextUtils.isEmpty(password)
                        && !TextUtils.isEmpty(passwordConfim) ) {
                    if ( password.equals(passwordConfim) ) {
                        mPref.edit()
                                .putString("password",
                                        MD5Utils.encode(password)).commit();
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this,
                                LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码不一致！",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}