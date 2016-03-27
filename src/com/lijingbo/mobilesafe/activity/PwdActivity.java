package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.utils.ToastUtils;

public class PwdActivity extends Activity {
    private EditText et_inputpassword;
    private ImageView iv_process_icon;
    private TextView tv_processname;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd);
        et_inputpassword = (EditText) findViewById(R.id.et_inputpassword);
        iv_process_icon = (ImageView) findViewById(R.id.iv_process_icon);
        tv_processname = (TextView) findViewById(R.id.tv_processname);
        packageName = getIntent().getStringExtra("packageName");
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            String name = applicationInfo.loadLabel(pm).toString();
            Drawable icon = applicationInfo.loadIcon(pm);
            iv_process_icon.setImageDrawable(icon);
            tv_processname.setText(name);
        } catch ( PackageManager.NameNotFoundException e ) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    public void ok(View view) {
        if ( TextUtils.isEmpty(et_inputpassword.getText().toString().trim()) ) {
            ToastUtils.showShortToast(PwdActivity.this, "密码不能为空");
            return;
        }
        if ( et_inputpassword.getText().toString().trim().equals("123") ) {
            Intent intent = new Intent();
            intent.setAction("com.lijingbo.mobilesafe.tempstop");
            intent.putExtra("packageName", packageName);
            sendBroadcast(intent);
            finish();
        } else {
            ToastUtils.showShortToast(PwdActivity.this, "请确认输入的密码正确");
        }
    }

    //重写返回键，点击返回键的时候，返回到桌面
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.HOME");
        startActivity(intent);
        super.onBackPressed();
    }
}
