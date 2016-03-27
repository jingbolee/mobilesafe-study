package com.lijingbo.mobilesafe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @FileName: com.lijingbo.mobilesafe.adapter.MyBaseAdapter.java
 * @Author: Li Jingbo
 * @Date: 2016-01-28 21:47
 * @Version V1.0 <描述当前版本功能>
 */
public abstract class MyBaseAdapter< T > extends BaseAdapter {

    public List< T > lists;
    public Context mContext;

    public MyBaseAdapter() {
        super();
    }

    public MyBaseAdapter(List< T > lists, Context mContext) {
        this.lists = lists;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);
}
