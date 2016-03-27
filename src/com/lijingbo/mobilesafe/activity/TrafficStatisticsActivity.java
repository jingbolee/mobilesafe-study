package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.bean.TrafficInfoBean;
import com.lijingbo.mobilesafe.db.dao.TrafficManagerDao;

import java.util.ArrayList;
import java.util.List;

public class TrafficStatisticsActivity extends Activity {
    private static final int DATA_FINISH = 4532;  //数据加载完成
    private ListView lv_traffic_count;
    private TextView tv_traffic_app_stats;
    private ProgressBar pb_traffic_loading;
    private List< TrafficInfoBean > trafficInfoBeans;  //应用数据集合
    private List< TrafficInfoBean > userTrafficInfoBeans;  //用户安装的应用流量集合
    private List< TrafficInfoBean > systemTrafficInfoBeans;  //系统应用流程集合
    private TrafficManagerDao dao;
    private PackageManager pm;

    private TrafficCountAdapter adapter;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pb_traffic_loading.setVisibility(View.INVISIBLE);
            tv_traffic_app_stats.setVisibility(View.VISIBLE);
            tv_traffic_app_stats.setText("用户程序(" + userTrafficInfoBeans.size() + ")");
            if ( msg.what == DATA_FINISH ) {
                if ( adapter == null ) {
                    adapter = new TrafficCountAdapter();
                    lv_traffic_count.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_statistics);
        lv_traffic_count = (ListView) findViewById(R.id.lv_traffic_count);
        pb_traffic_loading = (ProgressBar) findViewById(R.id.pb_traffic_loading);
        tv_traffic_app_stats = (TextView) findViewById(R.id.tv_traffic_app_stats);
        dao = new TrafficManagerDao(this);
        trafficInfoBeans = new ArrayList<>();
        userTrafficInfoBeans = new ArrayList<>();
        systemTrafficInfoBeans = new ArrayList<>();
        pm = getPackageManager();
        initData();

        lv_traffic_count.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ( firstVisibleItem <= userTrafficInfoBeans.size() ) {
                    tv_traffic_app_stats.setText("用户程序(" + userTrafficInfoBeans.size() + ")");
                } else {
                    tv_traffic_app_stats.setText("系统程序(" + systemTrafficInfoBeans.size() + ")");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        if ( adapter != null ) {
            adapter = null;
        }
        super.onDestroy();
    }

    //初始化数据
    private void initData() {
        pb_traffic_loading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List< ApplicationInfo > installedApplications = pm.getInstalledApplications(0);
                for ( ApplicationInfo app : installedApplications ) {
                    TrafficInfoBean bean = new TrafficInfoBean();
                    long[] data = dao.queryData(app.packageName);
                    bean.setIcon(app.loadIcon(pm));
                    bean.setPackname(app.packageName);
                    bean.setName(app.loadLabel(pm).toString());
                    bean.setRecdata(data[0] + "");
                    bean.setSenddata(data[1] + "");
                    bean.setDate("日期");
                    int flags = app.flags;
                    //判断是否是用户安装的应用
                    if ( (flags & ApplicationInfo.FLAG_SYSTEM) == 0 ) {
                        bean.setUserApp(true);
                    } else {
                        bean.setUserApp(false);
                    }
                    trafficInfoBeans.add(bean);
                }
                for ( TrafficInfoBean trafficInfo : trafficInfoBeans ) {
                    if ( trafficInfo.isUserApp() ) {
                        userTrafficInfoBeans.add(trafficInfo);
                    } else {
                        systemTrafficInfoBeans.add(trafficInfo);
                    }
                }
                mHandler.sendEmptyMessage(DATA_FINISH);
            }
        }).start();

    }


    class TrafficCountAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return trafficInfoBeans.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            return trafficInfoBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            TrafficInfoBean bean;
            if ( position == 0 ) {
                TextView tv = new TextView(TrafficStatisticsActivity.this);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("用户程序(" + userTrafficInfoBeans.size() + ")");
                return tv;
            } else if ( position == userTrafficInfoBeans.size() + 1 ) {
                TextView tv = new TextView(TrafficStatisticsActivity.this);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("系统程序(" + systemTrafficInfoBeans.size() + ")");
                return tv;
            } else if ( position <= userTrafficInfoBeans.size() ) {
                bean = userTrafficInfoBeans.get(position - 1);
            } else {
                bean = systemTrafficInfoBeans.get(position - userTrafficInfoBeans.size() - 1 - 1);
            }

            if ( convertView != null && convertView instanceof RelativeLayout ) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder = new ViewHolder();
                convertView = View.inflate(TrafficStatisticsActivity.this, R.layout.list_item_traffic, null);
                holder.iv_traffic_app_icon = (ImageView) convertView.findViewById(R.id.iv_traffic_app_icon);
                holder.tv_traffic_app_name = (TextView) convertView.findViewById(R.id.tv_traffic_app_name);
                holder.tv_traffic_app_mobilecount = (TextView) convertView.findViewById(R.id.tv_traffic_app_mobilecount);
                holder.tv_traffic_app_send = (TextView) convertView.findViewById(R.id.tv_traffic_app_send);
                holder.tv_traffic_app_rec = (TextView) convertView.findViewById(R.id.tv_traffic_app_rec);
                convertView.setTag(holder);
            }
            holder.iv_traffic_app_icon.setImageDrawable(bean.getIcon());
            holder.tv_traffic_app_name.setText(bean.getName());
            holder.tv_traffic_app_rec.setText("下载:" + Formatter.formatFileSize(TrafficStatisticsActivity.this, Long.parseLong(bean.getRecdata())));
            holder.tv_traffic_app_send.setText("上传:" + Formatter.formatFileSize(TrafficStatisticsActivity.this, Long.parseLong(bean.getSenddata())));
            holder.tv_traffic_app_mobilecount.setText(Formatter.formatFileSize(TrafficStatisticsActivity.this, Long.parseLong(bean.getRecdata()) + Long.parseLong(bean.getSenddata())));
            return convertView;
        }
    }


    static class ViewHolder {
        ImageView iv_traffic_app_icon;
        TextView tv_traffic_app_name;
        TextView tv_traffic_app_mobilecount;
        TextView tv_traffic_app_send;
        TextView tv_traffic_app_rec;
    }

}
