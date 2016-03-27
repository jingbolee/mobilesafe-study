package com.lijingbo.mobilesafe.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.bean.ContactsBean;

public class ContactsAdapter extends BaseAdapter {
	List<ContactsBean> contactsList;
	Context context;

	public ContactsAdapter(Context context, List<ContactsBean> contactsList) {
		this.context = context;
		this.contactsList = contactsList;
	}

	@Override
	public int getCount() {
		return contactsList.size();
	}

	@Override
	public Object getItem(int position) {
		return contactsList.get(position);
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
			view = View.inflate(context, R.layout.contacts_item_view, null);
			viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView) view.findViewById(R.id.tv_name);
			viewHolder.tvPhone = (TextView) view.findViewById(R.id.tv_phone);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder=(ViewHolder) view.getTag();
		}
		
		viewHolder.tvName.setText(contactsList.get(position).getName());
		viewHolder.tvPhone.setText(contactsList.get(position).getPhone());
		

		return view;
	}

	class ViewHolder {
		TextView tvName;
		TextView tvPhone;
	}

}
