package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lijingbo.mobilesafe.MyApplication;
import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.bean.VersionBean;
import com.lijingbo.mobilesafe.utils.LogUtils;
import com.lijingbo.mobilesafe.utils.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {
    protected static final int CODE_UPDATE_INFO = 0;
    protected static final int CODE_GO_HOME = 1;
    protected static final int CODE_URL_ERROR = 2;
    protected static final int CODE_NETWORK_ERROR = 3;
    private static final String TAG = "SplashActivity";

    private TextView tv_version;
    private TextView tv_showprogress;
    private RelativeLayout rl_root; // 根布局

    private SharedPreferences mPre;
    // 从服务器获取到的信息
    private static String mVersionName;
    private static String mDescription;
    private static int mVersionCode;
    private static String mDownLoadUrl;

    Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch ( msg.what ) {
                case CODE_UPDATE_INFO:
                    showUpdateDialog();
                    break;
                case CODE_NETWORK_ERROR:
                    Toast.makeText(MyApplication.getContext(), "网络错误",
                            Toast.LENGTH_SHORT).show();
                    goToHomeActivity();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(MyApplication.getContext(), "URL错误",
                            Toast.LENGTH_SHORT).show();
                    goToHomeActivity();
                    break;
                case CODE_GO_HOME:
                    goToHomeActivity();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        //拷贝号码归属地数据库
        copyDataBase("address.db");
        //拷贝病毒数据库
        copyDataBase("antivirus.db");
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("版本号：" + getAppVersionName());
        tv_showprogress = (TextView) findViewById(R.id.tv_showprogress);
        mPre = getSharedPreferences("config", MODE_PRIVATE);
        boolean autoUpdate = mPre.getBoolean("auto_update", true);
        if ( autoUpdate ) {
            checkVersion();
        } else {
            mHandler.sendEmptyMessageDelayed(CODE_GO_HOME, 2000);
        }
        createShortCut();
        // 渐变的动画效果
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
        anim.setDuration(2000);
        rl_root.startAnimation(anim);
    }


    //创建桌面快捷方式,在launcher上创建快捷方式，使用BroadCast来创建。发送一个广播，广播中携带action和extra。其中extra为需要创建的快捷方式，分别为图标，名称，和干什么的（intent）.

    private void createShortCut() {
        boolean shortcut = mPre.getBoolean("shortcut", false);
        if ( shortcut ) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机小助手");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        Intent shortIntent = new Intent();
        shortIntent.setAction("android.intent.action.MAIN");
        shortIntent.addCategory("android.intent.category.LAUNCHER");
        shortIntent.setClassName(getPackageName(), "com.lijingbo.mobilesafe.activity.SplashActivity");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortIntent);
        sendBroadcast(intent);
        mPre.edit().putBoolean("shortcut", true).commit();

    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /*
         * 安装取消，进入home activity
         *
         * @see android.app.Activity#onActivityResult(int, int,
         * android.content.Intent)
         */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        goToHomeActivity();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
     * 开启子线程从服务器获取最新的版本信息
     */
    private void checkVersion() {
        final long startTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                HttpURLConnection conn = null;
                try {
                    URL url = new URL("http://10.0.2.2:8080/update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");  //请求方法为GET
                    conn.setConnectTimeout(5000);  //链接超时为5秒
                    conn.setReadTimeout(5000);     //读取超时为5秒
                    conn.connect();
                    int responseCode = conn.getResponseCode();
                    if ( responseCode == 200 ) {
                        InputStream inputStream = conn.getInputStream();
                        String result = StreamUtils.readFromStream(inputStream);
                        VersionBean versionBean = parseJsonWithGson(result);
                        mVersionName = versionBean.getVersionName();
                        mDescription = versionBean.getDescription();
                        mVersionCode = versionBean.getVersionCode();
                        mDownLoadUrl = versionBean.getDownLoadUrl();
                        LogUtils.e(TAG, mDownLoadUrl);
                        if ( mVersionCode > getAppVersionCode() ) {
                            msg.what = CODE_UPDATE_INFO;
                        } else {
                            msg.what = CODE_GO_HOME;
                        }
                    }
                } catch ( MalformedURLException e ) {
                    msg.what = CODE_URL_ERROR;
                    e.printStackTrace();
                } catch ( IOException e ) {
                    msg.what = CODE_NETWORK_ERROR;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    long timeUsed = endTime - startTime;
                    if ( timeUsed < 2000 ) {
                        try {
                            Thread.sleep(2000 - timeUsed);
                        } catch ( InterruptedException e ) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                    if ( conn != null ) {
                        conn.disconnect();
                    }
                }

            }
        }).start();
    }

    /*
     * 显示版本提示对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                SplashActivity.this);
        builder.setCancelable(false);
        builder.setTitle("版本更新：" + mVersionName);
        builder.setMessage(mDescription);
        builder.setPositiveButton("现在更新", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoad();
            }
        });

        builder.setNegativeButton("以后更新", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToHomeActivity();
            }
        });

		/*
         * 按back键，进入home activity
		 */
        builder.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                goToHomeActivity();
            }
        });
        builder.show();
    }


    private void downLoad() {
        if ( Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ) {
            final String target = Environment.getExternalStorageDirectory()
                    + "/update.apk";
            HttpUtils http = new HttpUtils();
            http.download(mDownLoadUrl, target, new RequestCallBack< File >() {

                @Override
                public void onLoading(long total, long current,
                                      boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    tv_showprogress.setVisibility(View.VISIBLE);
//                    System.out.println("下载进度：" + current + "/" + total);
                    tv_showprogress.setText("下载进度：" + current * 100 / total
                            + "%");
                }

                @Override
                public void onSuccess(ResponseInfo< File > arg0) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(new File(target)),
                            "application/vnd.android.package-archive");
                    startActivityForResult(intent, 0);
                }

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    Toast.makeText(SplashActivity.this, "下载失败！！！",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(SplashActivity.this, "请检查sdcard！",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void goToHomeActivity() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private VersionBean parseJsonWithGson(String result) {
        Gson gson = new Gson();
        return gson.fromJson(result, VersionBean.class);
    }

    /*
     * 获取当前安装app的versionName
     */
    private String getAppVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            return packageInfo.versionName;
        } catch ( NameNotFoundException e ) {
            e.printStackTrace();
        }
        return "";
    }

    /*
     * 获取当前安装app的verisonCode
     */
    private int getAppVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            return packageInfo.versionCode;
        } catch ( NameNotFoundException e ) {
            e.printStackTrace();
        }

        return -1;
    }

    /*
     * app初始化的时候，把数据库从assert copy到files目录下。
     */
    private void copyDataBase(String baseName) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        File file = new File(getFilesDir(), baseName);
        if ( file.exists() ) {
            return;
        }
        try {
            outputStream = new FileOutputStream(file);
            inputStream = getAssets().open(baseName);
            int len;
            byte[] buffer = new byte[1024];
            while ( (len = inputStream.read(buffer)) != -1 ) {
                outputStream.write(buffer, 0, len);
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch ( Exception e ) {
                e.printStackTrace();
            }

        }
    }

}
