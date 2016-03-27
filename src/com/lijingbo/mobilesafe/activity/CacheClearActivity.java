package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.utils.LogUtils;
import com.lijingbo.mobilesafe.utils.ToastUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class CacheClearActivity extends Activity {
    private static final String TAG = "CacheClearActivity";
    private static final int GETCACHESIZE = 5674; //获取到了存在缓存的应用了
    private static final int GETOVER = 2345;   //获取缓存数据完毕
    private static final int REMOVECACHEOK = 23123;  //清楚cache成功
    private LinearLayout ll_cache_container;
    private TextView tv_cache_stats;
    private ProgressBar pb_cache_progress;
    private PackageManager mPm;
    private MyStatsObserver myStatsObserver;
    private MyStatsDataObserver myStatsDataObserver;
    private Method myUserId = null;
    private Method getPackageSizeInfo = null;

    private long cacheTotal = 0;
    private int count = 0; //扫描到的缓存数据数量
    private int allCount = 0;//所有应用的数量
    private int progress = 0;
    private List< PackageInfo > installedPackages;

    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ( msg.what == GETCACHESIZE ) {
                View view = View.inflate(CacheClearActivity.this, R.layout.list_item_cache, null);
                ImageView iv_cache_icon = (ImageView) view.findViewById(R.id.iv_cache_icon);
                TextView tv_cache_name = (TextView) view.findViewById(R.id.tv_cache_name);
                TextView tv_cache_cache = (TextView) view.findViewById(R.id.tv_cache_cache);
//                ImageView iv_cache_delete = (ImageView) view.findViewById(R.id.iv_cache_delete);
                Bundle bundle = msg.getData();
                final String packageName = bundle.getString("packageName");
                String cachesize = bundle.getString("cacheSize");
                try {
                    ApplicationInfo applicationInfo = mPm.getApplicationInfo(packageName, 0);
                    iv_cache_icon.setImageDrawable(applicationInfo.loadIcon(mPm));
                    tv_cache_name.setText(applicationInfo.loadLabel(mPm).toString());
                    tv_cache_cache.setText(cachesize);
                } catch ( PackageManager.NameNotFoundException e ) {
                    e.printStackTrace();
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 点击item，跳转到应用信息详情页面
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse("package:" + packageName));
                        startActivity(intent);
                    }
                });

//                iv_cache_delete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            deleteApplicationCacheFiles.invoke(mPm, packageName, myStatsDataObserver);
//                        } catch ( IllegalAccessException e ) {
//                            e.printStackTrace();
//                        } catch ( InvocationTargetException e ) {
//                            e.printStackTrace();
//                        }
//                    }
//                });


                progress++;
                pb_cache_progress.setProgress(progress);
                ll_cache_container.addView(view);
            } else if ( msg.what == GETOVER ) {
                pb_cache_progress.setVisibility(View.INVISIBLE);
                tv_cache_stats.setVisibility(View.VISIBLE);
                tv_cache_stats.setText("共扫描到" + count + "项缓存数据，总大小" + Formatter.formatFileSize(CacheClearActivity.this, cacheTotal));
            } else if ( msg.what == REMOVECACHEOK ) {
                ToastUtils.showShortToast(CacheClearActivity.this, "Cache清除干净了");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clear);
        tv_cache_stats = (TextView) findViewById(R.id.tv_cache_stats);
        ll_cache_container = (LinearLayout) findViewById(R.id.ll_cache_container);
        pb_cache_progress = (ProgressBar) findViewById(R.id.pb_cache_progress);
        mPm = getPackageManager();
        installedPackages = mPm.getInstalledPackages(0);
        pb_cache_progress.setMax(installedPackages.size());
        try {
            //通过反射，获取到UserHandle隐藏的方法myUserId()
            myUserId = UserHandle.class.getMethod("myUserId");
            //通过反射，获取到PackageManager隐藏的方法getPackageSizeInfo()
            getPackageSizeInfo = PackageManager.class.getMethod("getPackageSizeInfo", String.class, int.class, IPackageStatsObserver.class);
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        }
        myStatsObserver = new MyStatsObserver();
        myStatsDataObserver = new MyStatsDataObserver();
        for ( PackageInfo appInfo : installedPackages ) {
            try {
                getPackageSizeInfo.invoke(mPm, appInfo.packageName, myUserId.invoke(null), myStatsObserver);
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mHandle.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    class MyStatsObserver extends IPackageStatsObserver.Stub {
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            LogUtils.d(TAG, "-----------------Stub调用结果:" + succeeded);
            long cacheSize = pStats.cacheSize + pStats.externalCacheSize;
            allCount++;
            if ( cacheSize != 0 ) {
                count++;
                cacheTotal += cacheSize;
                Message msg = mHandle.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("cacheSize", Formatter.formatFileSize(CacheClearActivity.this, cacheSize));
                bundle.putString("packageName", pStats.packageName);
                msg.setData(bundle);
                msg.what = GETCACHESIZE;
                mHandle.sendMessage(msg);
            }
            if ( allCount == installedPackages.size() ) {
                mHandle.sendEmptyMessage(GETOVER);
            }
        }
    }

    class MyStatsDataObserver extends IPackageDataObserver.Stub {
        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            if ( succeeded ) {
                mHandle.sendEmptyMessage(REMOVECACHEOK);
            }
        }
    }

    public void clearAllCache(View v) throws InvocationTargetException, IllegalAccessException {
        try {
            Method freeStorageAndNotify = PackageManager.class.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
            freeStorageAndNotify.invoke(mPm, Integer.MAX_VALUE, myStatsDataObserver);
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        }

    }


}
