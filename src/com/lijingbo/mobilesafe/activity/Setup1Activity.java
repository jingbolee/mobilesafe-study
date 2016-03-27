package com.lijingbo.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.lijingbo.mobilesafe.R;

public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}

	@Override
	protected void goPreviousPage() {

	}

	@Override
	protected void goNextPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		// activity进入动画效果
		overridePendingTransition(R.anim.trans_next_in, R.anim.trans_next_out);

	}

}
