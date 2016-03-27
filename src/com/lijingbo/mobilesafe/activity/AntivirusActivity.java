package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

public class AntivirusActivity extends Activity {
    private static final String TAG = "AntivirusActivity";
    private static final int SCANNING = 7654;
    private static final int SCANNING_OVER = 909;
    private ImageView iv_scanning;
    private LinearLayout ll_container;
    private ProgressBar pb_antivirus_progress;
    private TextView tv_scaning_stats;
    private TextView tv_scaned_stats;
    private PackageManager pm;
    private int virus = 0;
    private int apk = 0;
    private int position = 0;
    Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ( msg.what == SCANNING ) {
                TextView tv = new TextView(AntivirusActivity.this);
                String name = msg.getData().getString("name");
                String result = msg.getData().getString("result");
                tv_scaning_stats.setText("正在扫描：" + name);
                apk++;
                if ( ("不是病毒").equals(result) ) {
                    tv.setText(name);
                    tv.setTextColor(Color.BLACK);
                    ll_container.addView(tv, position);
                } else {
                    virus++;
                    tv.setTextColor(Color.RED);
                    tv.setText(name + ":" + result);
                    ll_container.addView(tv, position);
                    position++;
                }
                tv_scaned_stats.setText("已扫描" + apk + "个软件，发现" + virus + "个病毒");
            } else if ( msg.what == SCANNING_OVER ) {
                iv_scanning.clearAnimation();
                tv_scaning_stats.setText("扫描完成");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        pb_antivirus_progress = (ProgressBar) findViewById(R.id.pb_antivirus_progress);
        tv_scaning_stats = (TextView) findViewById(R.id.tv_scaning_stats);
        tv_scaned_stats = (TextView) findViewById(R.id.tv_scaned_stats);
        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(1000);
        ra.setRepeatCount(Animation.INFINITE);
        iv_scanning.startAnimation(ra);

        pm = getPackageManager();
        scanningVirus();

    }

    private void scanningVirus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List< ApplicationInfo > installedApplications = pm.getInstalledApplications(0);
                pb_antivirus_progress.setMax(installedApplications.size());
                int progress = 0;
                for ( ApplicationInfo appinfo : installedApplications ) {
                    String name = appinfo.loadLabel(pm).toString();
                    String sourceDir = appinfo.sourceDir;
                    String apkmd5 = getAPKMD5(sourceDir);
                    String result = searchDB(apkmd5);
                    Message msg = mHandle.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("result", result);
                    msg.setData(bundle);
                    msg.what = SCANNING;
                    mHandle.sendMessage(msg);
                    progress++;
                    pb_antivirus_progress.setProgress(progress);
                }
                mHandle.sendEmptyMessage(SCANNING_OVER);
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        mHandle.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    //获取到安装应用的MD5
    private String getAPKMD5(String path) {
        try {
            File file = new File(path);
            StringBuffer sb = new StringBuffer();
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ( (len = fis.read(buffer)) != -1 ) {
                digest.update(buffer, 0, len);
            }
            byte[] result = digest.digest();
            for ( byte b : result ) {
                int i = b & 0xff; // 获取到第八位有效值
                String hexString = Integer.toHexString(i);
                if ( hexString.length() < 2 ) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            LogUtils.d(TAG, sb.toString());
            return sb.toString();
        } catch ( Exception e ) {
            e.printStackTrace();
            return "";
        }

    }


    //通过数据库查询病毒
    private String searchDB(String md5) {
        String result;
        File file = new File(getFilesDir(), "antivirus.db");
        final SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"desc"}, "md5=?", new String[]{md5}, null, null, null);
        if ( cursor.moveToNext() ) {
            result = cursor.getString(cursor.getColumnIndex("desc"));
        } else {
            result = "不是病毒";
        }
        cursor.close();
        db.close();
        return result;
    }
}
