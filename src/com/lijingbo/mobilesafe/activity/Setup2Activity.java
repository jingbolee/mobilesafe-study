package com.lijingbo.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.utils.ToastUtils;
import com.lijingbo.mobilesafe.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

    private SettingItemView siv_sim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        siv_sim = (SettingItemView) findViewById(R.id.siv_sim);

        String savedSim = mPref.getString("sim", null);
        if ( !TextUtils.isEmpty(savedSim) ) {
            siv_sim.setChecked(true);
        } else {
            siv_sim.setChecked(false);
        }

        siv_sim.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if ( siv_sim.isChecked() ) {
                    siv_sim.setChecked(false);
                    mPref.edit().remove("sim").commit();
                } else {
                    siv_sim.setChecked(true);
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();
                    mPref.edit().putString("sim", simSerialNumber).commit();
                }
            }
        });

    }

    @Override
    protected void goPreviousPage() {
        startActivity(new Intent(this, Setup1Activity.class));
        finish();
        overridePendingTransition(R.anim.trans_previous_in,
                R.anim.trans_previous_out);
    }

    @Override
    protected void goNextPage() {
        String savedSim = mPref.getString("sim", "");
        if ( TextUtils.isEmpty(savedSim) ) {
            ToastUtils.showShortToast(this, "请绑定SIM卡");
        } else {
            startActivity(new Intent(this, Setup3Activity.class));
            finish();
            overridePendingTransition(R.anim.trans_next_in, R.anim.trans_next_out);
        }


    }

}
