package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;

public class LostFindActivity extends Activity {

	private SharedPreferences mPref;
	private ImageView ivShowProtect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPref = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = mPref.getBoolean("configed", false);
		if (configed) {
			setContentView(R.layout.activity_lostfind);
			String savedSafePhone= mPref.getString("safeNumber", "");
			boolean protect = mPref.getBoolean("protect", false);
			TextView tvShouSaveNumber= (TextView) findViewById(R.id.tv_show_phone);
			tvShouSaveNumber.setText(savedSafePhone);
			ivShowProtect = (ImageView) findViewById(R.id.iv_showProtect);
			if (protect) {
				ivShowProtect.setImageResource(R.drawable.lock);
			}else {
				ivShowProtect.setImageResource(R.drawable.unlock);
			}
			
		} else {
			startActivity(new Intent(LostFindActivity.this,
					Setup1Activity.class));
			finish();
		}

	}

	public void reSetup(View v) {
		startActivity(new Intent(LostFindActivity.this, Setup1Activity.class));
		finish();
	}

}
