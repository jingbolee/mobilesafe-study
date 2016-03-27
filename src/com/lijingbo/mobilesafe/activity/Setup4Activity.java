package com.lijingbo.mobilesafe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.receiver.AdminReceiver;

public class Setup4Activity extends BaseSetupActivity {
    public static final int DPM_REQUEST_CODE = 201603;
    private CheckBox cbProtect;
	private ComponentName componentName;
	private DevicePolicyManager mDpm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		componentName = new ComponentName(Setup4Activity.this,
				AdminReceiver.class);
		mDpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		cbProtect = (CheckBox) findViewById(R.id.cb_protect);
		boolean protect = mPref.getBoolean("protect", false);
		if (protect) {
			cbProtect.setText("防盗保护已经开启");
            cbProtect.setChecked(true);
        } else {
            cbProtect.setText("防盗保护没有开启");
            cbProtect.setChecked(false);
        }
		cbProtect.setOnCheckedChangeListener(new OnCheckedChangeListener() {


			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					if (mDpm.isAdminActive(componentName)) {
						cbProtect.setText("防盗保护已经开启");
						mPref.edit().putBoolean("protect", true).commit();
					} else {
						//激活设备管理器
						Intent intent = new Intent(
								DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
						intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
								componentName);
						intent.putExtra(
								DevicePolicyManager.EXTRA_ADD_EXPLANATION,
								"提示文字");
                        startActivityForResult(intent, DPM_REQUEST_CODE);

					}

				} else {
					mDpm.removeActiveAdmin(componentName);
					cbProtect.setText("防盗保护没有开启");
					mPref.edit().putBoolean("protect", false).commit();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == DPM_REQUEST_CODE ) {
            if (resultCode == RESULT_OK) {
				cbProtect.setText("防盗保护已经开启");
				mPref.edit().putBoolean("protect", true).commit();
			} else {
				cbProtect.setText("防盗保护没有开启");
				cbProtect.setChecked(false);
				mPref.edit().putBoolean("protect", false).commit();
			}
		}
	}

	@Override
	protected void goPreviousPage() {
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		// activity点击previous动画效果
		overridePendingTransition(R.anim.trans_previous_in,
				R.anim.trans_previous_out);

	}

	@Override
	protected void goNextPage() {
		startActivity(new Intent(this, LostFindActivity.class));
		finish();
		// activity进入动画效果
		overridePendingTransition(R.anim.trans_next_in, R.anim.trans_next_out);
		mPref.edit().putBoolean("configed", true).commit();

	}

}
