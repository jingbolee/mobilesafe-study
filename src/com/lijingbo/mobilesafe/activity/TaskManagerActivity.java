package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.bean.TaskInfo;
import com.lijingbo.mobilesafe.engine.TaskInfoProvider;
import com.lijingbo.mobilesafe.utils.SystemInfoUtils;
import com.lijingbo.mobilesafe.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerActivity extends Activity {

    private static final int TASK_SETTING_RESULT = 19880321;

    private TextView tv_task_count;   //显示进程数量
    private TextView tv_mem_info;    //显示内存
    private LinearLayout ll_loading;
    private ListView lv_task_manager;
    private TextView tv_task_status;
    private List< TaskInfo > allTaskInfos;
    private List< TaskInfo > userTaskInfos;
    private List< TaskInfo > systemTaskInfos;

    private TaskInfoAdapter adapter;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ll_loading.setVisibility(View.INVISIBLE);
            if ( adapter == null ) {
                adapter = new TaskInfoAdapter();
                lv_task_manager.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
            tv_task_status.setVisibility(View.VISIBLE);
            tv_task_status.setText("用户进程(" + userTaskInfos.size() + ")");
            setTitle();
        }
    };


    private void setTitle() {
        if ( tasksystemvisible ) {
            runningTaskCount = allTaskInfos.size();
        } else {
            runningTaskCount = userTaskInfos.size();
        }
        tv_task_count.setText("运行中进程:" + runningTaskCount + "个");
        tv_mem_info.setText("剩余/总内存:" + Formatter.formatFileSize(TaskManagerActivity.this, avaliMem) + "/" + Formatter.formatFileSize(TaskManagerActivity.this, totalMem));
    }

    private long avaliMem;
    private long totalMem;
    private int runningTaskCount;  //显示运行的进程数量
    private SharedPreferences mPre;
    private boolean tasksystemvisible; //判断是否显示系统进程


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        mPre = getSharedPreferences("config", MODE_PRIVATE);
        tasksystemvisible = mPre.getBoolean("tasksystemvisible", false);
        allTaskInfos = new ArrayList<>();
        userTaskInfos = new ArrayList<>();
        systemTaskInfos = new ArrayList<>();
        tv_task_count = (TextView) findViewById(R.id.tv_task_count);
        tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
//        runningTaskCount = SystemInfoUtils.getRunningTaskCount(TaskManagerActivity.this);

        avaliMem = SystemInfoUtils.getAvaliMem(TaskManagerActivity.this);
        totalMem = SystemInfoUtils.getTotalMem(TaskManagerActivity.this);
        tv_mem_info.setText("剩余/总内存:" + Formatter.formatFileSize(TaskManagerActivity.this, avaliMem) + "/" + Formatter.formatFileSize(TaskManagerActivity.this, totalMem));


        lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_task_status = (TextView) findViewById(R.id.tv_task_status);

        //监听listview滚动
        lv_task_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ( firstVisibleItem <= userTaskInfos.size() ) {
                    tv_task_status.setText("用户进程(" + userTaskInfos.size() + ")");
                } else {
                    tv_task_status.setText("系统进程(" + systemTaskInfos.size() + ")");
                }
            }
        });


        lv_task_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView< ? > parent, View view, int position, long id) {
                TaskInfo taskInfo;
                ViewHolder holder = (ViewHolder) view.getTag();
                if ( position == 0 ) {
                    return;
                } else if ( position == userTaskInfos.size() + 1 ) {
                    return;
                } else if ( position <= userTaskInfos.size() ) {
                    taskInfo = userTaskInfos.get(position - 1);

                    if ( taskInfo.isChecked() ) {
                        taskInfo.setIsChecked(false);
                        holder.cb_task_clear.setChecked(false);
                    } else {
                        taskInfo.setIsChecked(true);
                        holder.cb_task_clear.setChecked(true);
                    }
                } else {
                    taskInfo = systemTaskInfos.get(position - 1 - 1 - userTaskInfos.size());
                    if ( taskInfo.isChecked() ) {
                        taskInfo.setIsChecked(false);
                        holder.cb_task_clear.setChecked(false);
                    } else {
                        taskInfo.setIsChecked(true);
                        holder.cb_task_clear.setChecked(true);
                    }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        initData();

    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        if ( adapter != null ) {
            adapter = null;
        }
        super.onDestroy();
    }

    private void initData() {
        allTaskInfos.clear();
        userTaskInfos.clear();
        systemTaskInfos.clear();
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                allTaskInfos = TaskInfoProvider.getTaskInfoLists(TaskManagerActivity.this);
                for ( TaskInfo taskInfo : allTaskInfos ) {
                    if ( taskInfo.isUserApp() ) {
                        userTaskInfos.add(taskInfo);
                    } else {
                        systemTaskInfos.add(taskInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }


    class TaskInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if ( tasksystemvisible ) {
                return systemTaskInfos.size() + 1 + userTaskInfos.size() + 1;
            } else {
                return userTaskInfos.size() + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            if ( tasksystemvisible ) {
                return allTaskInfos.get(position);
            } else {
                return userTaskInfos.get(position);
            }

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskInfo taskInfo;
            if ( position == 0 ) {
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("用户进程(" + userTaskInfos.size() + ")");
                return tv;
            } else if ( position == (userTaskInfos.size() + 1) ) {
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("系统进程(" + systemTaskInfos.size() + ")");
                return tv;
            } else if ( position <= userTaskInfos.size() ) {
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                taskInfo = systemTaskInfos.get(position - 1 - 1 - userTaskInfos.size());
            }
            ViewHolder holder;
            if ( convertView != null && convertView instanceof RelativeLayout ) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder = new ViewHolder();
                convertView = View.inflate(TaskManagerActivity.this, R.layout.task_manager_item_view, null);
                holder.iv_task_icon = (ImageView) convertView.findViewById(R.id.iv_task_icon);
                holder.tv_task_name = (TextView) convertView.findViewById(R.id.tv_task_name);
                holder.tv_task_mem = (TextView) convertView.findViewById(R.id.tv_task_mem);
                holder.cb_task_clear = (CheckBox) convertView.findViewById(R.id.cb_task_clear);
                convertView.setTag(holder);
            }
            holder.iv_task_icon.setImageDrawable(taskInfo.getIcon());
            holder.tv_task_name.setText(taskInfo.getName());
            holder.tv_task_mem.setText("内存占用:" + Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemSize()));
            holder.cb_task_clear.setChecked(taskInfo.isChecked());
            if ( getPackageName().equals(taskInfo.getPackageName()) ) {
                holder.cb_task_clear.setVisibility(View.INVISIBLE);
            } else {
                //checkbox需要复用，所以需要写else的代码
                holder.cb_task_clear.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_task_icon;
        TextView tv_task_name;
        TextView tv_task_mem;
        CheckBox cb_task_clear;
    }

    //全选
    public void selectAll(View view) {
        if ( tasksystemvisible ) {
            for ( TaskInfo info : allTaskInfos ) {
                //当是该应用程序的时候，不处理，直接跳出这次循环
                if ( getPackageName().equals(info.getPackageName()) ) {
                    continue;
                }
                info.setIsChecked(true);
            }
        } else {
            for ( TaskInfo info : userTaskInfos ) {
                if ( getPackageName().equals(info.getPackageName()) ) {
                    continue;
                }
                info.setIsChecked(true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    //反选
    public void selectOppo(View view) {
        if ( tasksystemvisible ) {
            for ( TaskInfo info : allTaskInfos ) {
                //当是该应用程序的时候，不处理，直接跳出这次循环
                if ( getPackageName().equals(info.getPackageName()) ) {
                    continue;
                }
                info.setIsChecked(!info.isChecked());
            }
        } else {
            for ( TaskInfo info : userTaskInfos ) {
                //当是该应用程序的时候，不处理，直接跳出这次循环
                if ( getPackageName().equals(info.getPackageName()) ) {
                    continue;
                }
                info.setIsChecked(!info.isChecked());
            }

        }
        adapter.notifyDataSetChanged();
    }

    //清理进程
    public void kill(View view) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List< TaskInfo > removeTasks = new ArrayList<>();
        int taskNumber = 0;
        int mem = 0;
        if ( tasksystemvisible ) {
            removeTasks.clear();
            for ( TaskInfo info : allTaskInfos ) {
                if ( info.isChecked() ) {
                    am.killBackgroundProcesses(info.getPackageName());
                    if ( info.isUserApp() ) {
                        //当是该应用程序的时候，不处理，直接跳出这次循环
                        if ( getPackageName().equals(info.getPackageName()) ) {
                            continue;
                        }
                        userTaskInfos.remove(info);
                    } else {
                        systemTaskInfos.remove(info);
                    }
                    taskNumber += 1;
                    mem += (info.getMemSize() / 1024) / 1024;

                    avaliMem = avaliMem + info.getMemSize();
                    runningTaskCount = runningTaskCount - 1;
                    removeTasks.add(info);
                }

            }
            allTaskInfos.removeAll(removeTasks);
        } else {
            removeTasks.clear();
            //迭代过程中，不能操作数据，即在for循环中，不能对数据userTaskInfos进行移出操作。所以需要使用removeTasks。
            for ( TaskInfo info : userTaskInfos ) {
                if ( info.isChecked() ) {
                    am.killBackgroundProcesses(info.getPackageName());
                    if ( getPackageName().equals(info.getPackageName()) ) {
                        continue;
                    }
                    taskNumber += 1;
                    mem += (info.getMemSize() / 1024) / 1024;
                    avaliMem = avaliMem + info.getMemSize();
                    runningTaskCount = runningTaskCount - 1;
                    removeTasks.add(info);
                }
            }
            userTaskInfos.removeAll(removeTasks);
        }

//        tv_task_count.setText("运行中进程:" + runningTaskCount + "个");
//        tv_mem_info.setText("剩余/总内存:" + Formatter.formatFileSize(TaskManagerActivity.this, avaliMem) + "/" + Formatter.formatFileSize(TaskManagerActivity.this, totalMem));
        setTitle();
        adapter.notifyDataSetChanged();
        ToastUtils.showShortToast(this, "共清理了" + taskNumber + "个程序，节省了" + mem + "MB内存");

    }

    //进入设置
    public void goSetting(View view) {
        Intent intent = new Intent(TaskManagerActivity.this, TaskSettingActivity.class);
        startActivityForResult(intent, TASK_SETTING_RESULT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == RESULT_OK ) {
            switch ( requestCode ) {
                case TASK_SETTING_RESULT:
                    tasksystemvisible = data.getBooleanExtra("tasksystemvisible", this.tasksystemvisible);
                    setTitle();
                    break;
            }
        }
        adapter.notifyDataSetChanged();


    }
}
