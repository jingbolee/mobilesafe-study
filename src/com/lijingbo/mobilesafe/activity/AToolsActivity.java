package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.utils.SmsUtils;
import com.lijingbo.mobilesafe.utils.ToastUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/*
 * 高级工具
 */
public class AToolsActivity extends Activity {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /**
     * 号码归属地查询
     *
     * @param v
     */
    public void numberAddressQuery(View v) {
        startActivity(new Intent(this, AddressQueryActivity.class));
    }

    /**
     * 短信备份
     *
     * @param v
     */
    public void smsBackUp(View v) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在备份短信");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //使用回调函数，解耦
                    SmsUtils.backUpSms(AToolsActivity.this, new SmsUtils.BackUpCallBack() {
                        @Override
                        public void beforeSmsBackUp(int max) {
                            progressDialog.setMax(max);
                        }

                        @Override
                        public void onSmsBackUp(int progress) {
                            progressDialog.setProgress(progress);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showShortToast(AToolsActivity.this, "备份成功");
                        }
                    });

                } catch ( IOException e ) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showShortToast(AToolsActivity.this, "备份失败");
                        }
                    });
                } finally {
                    progressDialog.dismiss();
                }
            }
        }).start();

    }

    /**
     * 短信还原
     *
     * @param v
     */
    public void smsRestore(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("恢复短信内容");
        builder.setMessage("是否要删除现有的短信内容？");
        builder.setNegativeButton("保留", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                backUpProgress(false);
            }
        });
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                backUpProgress(true);
            }
        });
        builder.show();

    }

    private void backUpProgress(final boolean delPhoneSms) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在还原短信");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SmsUtils.restoreSms(AToolsActivity.this, delPhoneSms, new SmsUtils.RestoreCallBack() {
                        @Override
                        public void beforeSmsBackUp(int max) {
                            progressDialog.setMax(max);
                        }

                        @Override
                        public void onSmsBackUp(int progress) {
                            progressDialog.setProgress(progress);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showShortToast(AToolsActivity.this, "还原成功");
                        }
                    });

                } catch ( IOException e ) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showShortToast(AToolsActivity.this, "还原失败1");
                        }
                    });

                } catch ( XmlPullParserException e ) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showShortToast(AToolsActivity.this, "还原失败2");
                        }
                    });

                } finally {
                    progressDialog.dismiss();
                }
            }
        }).start();

    }

}
