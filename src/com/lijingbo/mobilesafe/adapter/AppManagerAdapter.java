package com.lijingbo.mobilesafe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @FileName: com.lijingbo.mobilesafe.adapter.AppManagerAdapter.java
 * @Author: Li Jingbo
 * @Date: 2016-02-23 10:16
 * @Version V1.0 <描述当前版本功能>
 */
public class AppManagerAdapter extends BaseAdapter {

    private List< AppInfo > appInfos = new ArrayList<>();
    private Context context;

    private List< AppInfo > userInstallApps = new ArrayList<>();
    private List< AppInfo > systemApps = new ArrayList<>();

    public AppManagerAdapter(List< AppInfo > lists, Context mContext) {

        this.appInfos.addAll(lists);
        this.context = mContext;
        fileData(appInfos);


    }

    private void fileData(List< AppInfo > lists) {
        //根据是否是用户安装的程序，把app分为系统和用户
        if ( userInstallApps.size() != 0 || systemApps.size() != 0 ) {
            userInstallApps.clear();
            systemApps.clear();
        }

        for ( AppInfo app : lists ) {
            if ( app.isUserApp() ) {
                userInstallApps.add(app);
            } else {
                systemApps.add(app);
            }
        }
    }

    //需要添加2个文本标签分别显示用户程序和系统程序，所有在数量上分别加1
    @Override
    public int getCount() {
        return userInstallApps.size() + 1 + systemApps.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return appInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AppInfo appInfo;
        if ( position == 0 ) {
            TextView tv = new TextView(context);
            tv.setTextColor(Color.WHITE);
            tv.setBackgroundColor(Color.GRAY);
            tv.setText("用户程序 : " + userInstallApps.size() + "个");
            return tv;
        } else if ( (position - userInstallApps.size() - 1) == 0 ) {
            TextView tv = new TextView(context);
            tv.setTextColor(Color.WHITE);
            tv.setBackgroundColor(Color.GRAY);
            tv.setText("系统程序 : " + systemApps.size() + "个");
            return tv;
        } else if ( position <= userInstallApps.size() ) {
            int newposition = position - 1;        //用户程序位置，需要先减掉一个文本标签所占用的位置
            appInfo = userInstallApps.get(newposition);
        } else {
            int newposition = position - 1 - 1 - userInstallApps.size();   //系统程序位置，需要先减掉2个文本标签所占用的位置和用户程序的个数
            appInfo = systemApps.get(newposition);
        }
        View view;
        ViewHolder viewHolder;
        //判断view，是否为空和该view是否为需要复用view的缓存
        if ( convertView != null && convertView instanceof RelativeLayout ) {
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            view = View.inflate(context, R.layout.app_manager_item_view, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tv_is_rom = (TextView) view.findViewById(R.id.tv_is_rom);
            viewHolder.iv_islock = (ImageView) view.findViewById(R.id.iv_islock);
//            viewHolder.btn_delete_app = (Button) view.findViewById(R.id.btn_delete_app);
            view.setTag(viewHolder);
        }
        viewHolder.iv_icon.setImageDrawable(appInfo.getIcon());
        viewHolder.tv_name.setText(appInfo.getName());
        if ( appInfo.isInRom() ) {
            viewHolder.tv_is_rom.setText("手机内存");
        } else {
            viewHolder.tv_is_rom.setText("外部存储");
        }
        if ( appInfo.isLock() ) {
            viewHolder.iv_islock.setImageResource(R.drawable.lock);
        } else {
            viewHolder.iv_islock.setImageResource(R.drawable.unlock);
        }

//        viewHolder.btn_delete_app.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("删除程序：" + appInfo.getName());
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.VIEW");
//                intent.setAction("android.intent.action.DELETE");
//                intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
//                context.startActivity(intent);
//
//                appInfos.remove(appInfo);
//                notifyDataSetChanged();
//
//            }
//        });
        return view;
    }

    public void refushData(List< AppInfo > lists) {
        if ( appInfos.size() != 0 ) {
            appInfos.clear();
            appInfos.addAll(lists);
        }
        fileData(appInfos);
        notifyDataSetChanged();

    }


    public static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_is_rom;
        ImageView iv_islock;
//        Button btn_delete_app;
    }
}
