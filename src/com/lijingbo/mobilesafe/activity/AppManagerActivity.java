package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.adapter.AppManagerAdapter;
import com.lijingbo.mobilesafe.bean.AppInfo;
import com.lijingbo.mobilesafe.db.dao.LockPackagenameDao;
import com.lijingbo.mobilesafe.engine.AppInfoProvider;
import com.lijingbo.mobilesafe.utils.DensityUtil;
import com.lijingbo.mobilesafe.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity implements View.OnClickListener {

    public static final int UNINSTALL_APP = 111;

    private TextView tv_avali_rom;
    private TextView tv_avali_sd;
    private ListView lv_app_manager;
    private LinearLayout ll_loading;

    private List< AppInfo > appInfos = new ArrayList<>();
    private List< AppInfo > userInstallApps = new ArrayList<>();
    private List< AppInfo > systemApps = new ArrayList<>();

    private TextView tv_app_status;
    private PopupWindow popupWindow;

    private PackageManager pm;
    private AppManagerAdapter adapter;

    private AppInfo app;
    /*
    * 定义弹出框中卸载，启动和分享
     */
    private LinearLayout ll_uninstall_app;
    private LinearLayout ll_start_app;
    private LinearLayout ll_share_app;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ll_loading.setVisibility(View.INVISIBLE);
            tv_app_status.setVisibility(View.VISIBLE);
            tv_app_status.setText("用户程序 : " + userInstallApps.size() + "个");
            if ( adapter == null ) {
                adapter = new AppManagerAdapter(appInfos, AppManagerActivity.this);
                lv_app_manager.setAdapter(adapter);
            } else {
                adapter.refushData(appInfos);
            }
        }
    };
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initUi();
        initData();


        final long avaliableRom = getAvaliableSpace(Environment.getDataDirectory().getAbsolutePath());
        long avaliableSD = getAvaliableSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        tv_avali_rom.setText("内存可用：" + Formatter.formatFileSize(this, avaliableRom));
        tv_avali_sd.setText("外部可用：" + Formatter.formatFileSize(this, avaliableSD));
        //当长按锁定或者解锁程序时，发送数据改变的广播
        intent = new Intent();
        intent.setAction("com.lijingbo.mobilesafe.datachanged");

    }

    @Override
    protected void onDestroy() {
        dismissPopupWindow();
        if ( adapter != null ) {
            adapter = null;
        }
        //清空队列，是系统可以回收mHandler.
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    private void dismissPopupWindow() {
        if ( popupWindow != null && popupWindow.isShowing() ) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    private void initUi() {
        tv_avali_rom = (TextView) findViewById(R.id.tv_avali_rom);
        tv_avali_sd = (TextView) findViewById(R.id.tv_avali_sd);
        tv_app_status = (TextView) findViewById(R.id.tv_app_status);
        lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

        tv_app_status.setVisibility(View.INVISIBLE);


        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView< ? > parent, View view, int position, long id) {
                dismissPopupWindow();

                if ( position <= userInstallApps.size() && position > 0 ) {
                    app = userInstallApps.get(position - 1);
                } else if ( position == 0 || position == userInstallApps.size() + 1 ) {
                    return;
                } else {
                    app = systemApps.get(position - 1 - 1 - userInstallApps.size());
                }

                View converview = View.inflate(AppManagerActivity.this, R.layout.popup_app_manager, null);
                ll_uninstall_app = (LinearLayout) converview.findViewById(R.id.ll_uninstall_app);
                ll_start_app = (LinearLayout) converview.findViewById(R.id.ll_start_app);
                ll_share_app = (LinearLayout) converview.findViewById(R.id.ll_share_app);

                ll_uninstall_app.setOnClickListener(AppManagerActivity.this);
                ll_start_app.setOnClickListener(AppManagerActivity.this);
                ll_share_app.setOnClickListener(AppManagerActivity.this);

                int[] location = new int[2];
                view.getLocationInWindow(location);
                int x = DensityUtil.dip2px(AppManagerActivity.this, 60);

                popupWindow = new PopupWindow(converview, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                //popupWindow不提供背景色，需要设置动画，必须设置一个背景色
                //设置背景色为透明色
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, x, location[1]);

                //设置popupWindow的动画效果
                //设置缩放动画
                ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                sa.setDuration(300);
                //设置透明动画
                AlphaAnimation aa = new AlphaAnimation(0.5f, 1f);
                aa.setDuration(300);
                //把缩放动画和透明动画放在动画集合里，
                // AnimationSet的参数shareInterpolator：是个布尔值，为True时，所有的动画一起执行；为false时，动画分别执行。
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(sa);
                set.addAnimation(aa);
                converview.startAnimation(set);
            }
        });

        //listview设置滚动监听
        lv_app_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            //监听滚动状态
            //firstVisibleItem：the index of the first visible cell
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPopupWindow();
                if ( firstVisibleItem > userInstallApps.size() ) {
                    tv_app_status.setText("系统程序 : " + systemApps.size() + "个");
                } else {
                    tv_app_status.setText("用户程序 : " + userInstallApps.size() + "个");
                }
            }
        });

        //长按，实现锁定程序
        lv_app_manager.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView< ? > parent, View view, int position, long id) {
                AppInfo appInfo;
                LockPackagenameDao dao = new LockPackagenameDao(AppManagerActivity.this);
                if ( position == 0 || position == userInstallApps.size() + 1 ) {
                    return true;
                } else if ( position <= userInstallApps.size() && position > 0 ) {
                    appInfo = userInstallApps.get(position - 1);
                } else {
                    appInfo = systemApps.get(position - 1 - 1 - userInstallApps.size());
                }
                ImageView iv_islock = (ImageView) view.findViewById(R.id.iv_islock);

                if ( appInfo.isLock() ) {
                    appInfo.setIsLock(false);
                    dao.deletePackagename(appInfo.getPackageName());
                    iv_islock.setImageResource(R.drawable.unlock);
                    sendBroadcast(intent);
                } else {
                    appInfo.setIsLock(true);
                    dao.addPackagename(appInfo.getPackageName());
                    iv_islock.setImageResource(R.drawable.lock);
                    sendBroadcast(intent);
                }
                return true;
            }
        });
    }


    private void initData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if ( appInfos.size() != 0 ) {
                    appInfos.clear();
                }
                appInfos.addAll(AppInfoProvider.getAppInfo(AppManagerActivity.this));
                if ( userInstallApps.size() != 0 || systemApps.size() != 0 ) {
                    userInstallApps.clear();
                    systemApps.clear();
                }
                for ( AppInfo app : appInfos ) {
                    if ( app.isUserApp() ) {
                        userInstallApps.add(app);
                    } else {
                        systemApps.add(app);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private long getAvaliableSpace(String path) {
        StatFs statFs = new StatFs(path);
        if ( Build.VERSION.SDK_INT >= 18 ) {
            return statFs.getAvailableBytes();
        } else {
            long blockSize = statFs.getBlockSize();
            long availableBlocks = statFs.getAvailableBlocks();
            return blockSize * availableBlocks;
        }
    }


    @Override
    public void onClick(View v) {
        switch ( v.getId() ) {
            case R.id.ll_uninstall_app:
                if ( app.isUserApp() ) {
                    uninstallApp();
                } else {
//                    Runtime.getRuntime().exec();
                    ToastUtils.showShortToast(AppManagerActivity.this, "需要获取到root权限以后才能卸载该软件");
                }

                dismissPopupWindow();
                break;
            case R.id.ll_share_app:
                shardApp();
                dismissPopupWindow();
                break;
            case R.id.ll_start_app:
                startApp();
                dismissPopupWindow();
                break;
        }
    }

    //分享应用
    private void shardApp() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "向你推荐一款软件，软件名：" + app.getName());
        startActivity(intent);
    }

    //卸载应用
    public void uninstallApp() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setAction("android.intent.action.DELETE");
        intent.setData(Uri.parse("package:" + app.getPackageName()));
        startActivityForResult(intent, UNINSTALL_APP);

    }

    //启动应用
    private void startApp() {
        pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(app.getPackageName());
        if ( intent != null ) {
            startActivity(intent);
        } else {
            ToastUtils.showShortToast(AppManagerActivity.this, "该应用无法启动");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == UNINSTALL_APP ) {
            System.out.println("卸载应用的返回码：" + resultCode);
//            if ( resultCode == RESULT_OK ) {
            initData();
//            } else {
//                ToastUtils.showShortToast(AppManagerActivity.this, "卸载应用失败!");
//            }
        }
    }


}
