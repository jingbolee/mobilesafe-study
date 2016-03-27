package com.lijingbo.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;

public class SettingItemView extends RelativeLayout {
	// 命名空间，与布局文件中写的命名空间保持一致
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.lijingbo.mobilesafe";
	private TextView tv_title;
	private TextView tv_desc;
	private CheckBox cb_status;
	private String mTitle;
	private String mUpdateOn;
	private String mUpdateOff;

	public SettingItemView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initview();
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initview();
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTitle = attrs.getAttributeValue(NAMESPACE, "title");
		mUpdateOn = attrs.getAttributeValue(NAMESPACE, "update_on");
		mUpdateOff = attrs.getAttributeValue(NAMESPACE, "update_off");

		initview();
	}

	public SettingItemView(Context context) {
		super(context);
		initview();
	}

	private void initview() {
		View.inflate(getContext(), R.layout.setting_item_view, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		setTitle(mTitle);
	}

	// 设置item的名称
	public void setTitle(String text) {
		tv_title.setText(text);
	}

	// 设置item的描述
	public void setDesc(String text) {
		tv_desc.setText(text);
	}

	// 获取checkbox是否勾选
	public boolean isChecked() {
		return cb_status.isChecked();
	}

	// 设置checkbox勾选状态
	public void setChecked(boolean check) {
		if (check) {
			setDesc(mUpdateOn);
		} else {
			setDesc(mUpdateOff);
		}
		cb_status.setChecked(check);
	}

}
