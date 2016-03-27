package com.lijingbo.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.utils.ToastUtils;

public class Setup3Activity extends BaseSetupActivity {

	private static final int CODE_CONTACTS_PHONE = 101;
	private EditText etSafeNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		etSafeNumber = (EditText) findViewById(R.id.et_safeNumber);
		String savedPhone = mPref.getString("safeNumber", "");
		setSafeNumber(savedPhone);

	}

	@Override
	protected void goPreviousPage() {
		saveEditTextPhone();
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		// activity点击previous动画效果
		overridePendingTransition(R.anim.trans_previous_in,
				R.anim.trans_previous_out);

	}

	private void setSafeNumber(String text) {
		etSafeNumber.setText(text);
	}
	
	private void saveEditTextPhone(){
		String etNumber = etSafeNumber.getText().toString().trim();
		mPref.edit().putString("safeNumber", etNumber).commit();
	}

	@Override
	protected void goNextPage() {
		String etNumber = etSafeNumber.getText().toString().trim();
		if (TextUtils.isEmpty(etNumber)) {
			ToastUtils.showShortToast(this, "请输入安全号码");
			return;
		} else {
			saveEditTextPhone();
			startActivity(new Intent(this, Setup4Activity.class));
			finish();
			// activity进入动画效果
			overridePendingTransition(R.anim.trans_next_in,
					R.anim.trans_next_out);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CODE_CONTACTS_PHONE) {
			if (resultCode == RESULT_OK) {
				String phone = data.getExtras().getString("phone");
				phone = phone.replaceAll("-", "").replaceAll(" ", "");
				setSafeNumber(phone);
				mPref.edit().putString("safeNumber", phone).commit();
			}
		}
	}

	public void getSafeNumber(View v) {
		startActivityForResult(new Intent(Setup3Activity.this,
				ContactsActivity.class), CODE_CONTACTS_PHONE);
	}

}
