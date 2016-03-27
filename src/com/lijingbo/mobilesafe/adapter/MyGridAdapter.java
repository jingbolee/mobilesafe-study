package com.lijingbo.mobilesafe.adapter;

import com.lijingbo.mobilesafe.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MyGridAdapter extends BaseAdapter implements ListAdapter {

	private String[] mItems;
	private int[] mPics;
	private Context context;

	public MyGridAdapter(Context context, String[] mItems, int[] mPics) {
		this.context = context;
		this.mItems = mItems;
		this.mPics = mPics;
	}

	@Override
	public int getCount() {
		return mItems.length;
	}

	@Override
	public Object getItem(int position) {
		return mItems[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = View.inflate(context, R.layout.home_item_view, null);
			viewHolder = new ViewHolder();
			viewHolder.iv_item = (ImageView) view.findViewById(R.id.iv_item);
			viewHolder.tv_item = (TextView) view.findViewById(R.id.tv_item);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.iv_item.setImageResource(mPics[position]);
		viewHolder.tv_item.setText(mItems[position]);
		return view;
	}

	class ViewHolder {
		ImageView iv_item;
		TextView tv_item;
	}

}
