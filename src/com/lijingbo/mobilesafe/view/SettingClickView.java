package com.lijingbo.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;

public class SettingClickView extends RelativeLayout {
	// 命名空间，与布局文件中写的命名空间保持一致
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.lijingbo.mobilesafe";
	private TextView tv_title;
	private TextView tv_desc;
	private String mTitle;
	private String mDesc;

	public SettingClickView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initview();
	}

	public SettingClickView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initview();
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTitle = attrs.getAttributeValue(NAMESPACE, "title");
		mDesc=attrs.getAttributeValue(NAMESPACE, "desc");
		initview();
	}

	public SettingClickView(Context context) {
		super(context);
		initview();
	}

	private void initview() {
		View.inflate(getContext(), R.layout.setting_item_click, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		setTitle(mTitle);
		setDesc(mDesc);
	}

	// 设置item的名称
	public void setTitle(String text) {
		tv_title.setText(text);
	}

	// 设置item的描述
	public void setDesc(String text) {
		tv_desc.setText(text);
	}

}
