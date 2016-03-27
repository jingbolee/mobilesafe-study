package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.db.dao.AddressDao;

public class AddressQueryActivity extends Activity {

	private EditText etNumber;
	private TextView tvShowAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addressquery);
		etNumber = (EditText) findViewById(R.id.et_number);
		tvShowAddress = (TextView) findViewById(R.id.tv_showaddress);
		// 添加文本框监听器，当Edittext中输入文字变化是，监听
		etNumber.addTextChangedListener(new TextWatcher() {
			// 文字改变时
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// if (TextUtils.isEmpty(s.toString())) {
				tvShowAddress.setText(AddressDao.getAddress(s.toString()));
				// }

			}

			// 文字改变前
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			// 文字改变后
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	public void query(View v) {
		String number = etNumber.getText().toString();
		if (!TextUtils.isEmpty(number)) {
			tvShowAddress.setText(AddressDao.getAddress(number));
		} else {
			//对输入框etNumber实现抖动效果，实现原理通过插补器Interpolator实现。
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			etNumber.startAnimation(shake);
			vibrate();
		}

	}
	//使用振动功能，需要申请权限
	private void vibrate(){
		Vibrator vibrator=(Vibrator) getSystemService(VIBRATOR_SERVICE);
		//设置振动时长，为4秒
//		vibrator.vibrate(4000);
		//设置振动规律
		//参数1：振动数组，先等待1秒，在振动2秒，在等待1秒，在振动3秒，在等待1秒，在振动2秒
		//参数2：-1代表循环一次；假如设置为0代表从振动数组的第0个位置开始循环振动，1代表从振动数组的第1个位置开始循环振动
		vibrator.vibrate(new long[]{1000,2000,1000,3000,1000,2000}, -1);
//		vibrator.cancel()        //取消振动
	}

}
