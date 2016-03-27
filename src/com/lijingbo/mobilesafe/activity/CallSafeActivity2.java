package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.adapter.BlackNumberAdapter;
import com.lijingbo.mobilesafe.bean.BlackNumberInfo;
import com.lijingbo.mobilesafe.db.dao.BlackNumberDao;
import com.lijingbo.mobilesafe.utils.ToastUtils;

import java.util.List;

/**
 * 分批加载数据，就是下拉刷新功能
 */
public class CallSafeActivity2 extends Activity {

    private ListView list_view;
    private LinearLayout ll_show;

    private BlackNumberDao dao;
    private List< BlackNumberInfo > blackNumberInfos;
    private BlackNumberAdapter adapter;
    private int mStartIndex = 0;
    private int mMaxCount = 20;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ll_show.setVisibility(View.INVISIBLE);
            if ( adapter == null ) {
                adapter = new BlackNumberAdapter(blackNumberInfos, CallSafeActivity2.this);
                list_view.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

        }
    };
    private int totalNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe2);
        initData();
        initUI();


    }

    private void initData() {
        dao = new BlackNumberDao(CallSafeActivity2.this);
        totalNumber = dao.getCount();
//        System.out.println("总共含有：" + totalNumber);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if ( blackNumberInfos == null ) {
                    blackNumberInfos = dao.findPage2(mStartIndex, mMaxCount);
                } else {
                    blackNumberInfos.addAll(dao.findPage2(mStartIndex, mMaxCount));
                }

                handler.sendEmptyMessage(0);
            }
        }).start();

    }


    private void initUI() {
        list_view = (ListView) findViewById(R.id.list_view);
        ll_show = (LinearLayout) findViewById(R.id.ll_show);

        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {

            /**
             * 状态改变时调用
             * @param view
             * @param scrollState
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                System.out.println("onScrollStateChanged");
                switch ( scrollState ) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        int lastVisiblePosition = list_view.getLastVisiblePosition();
                        if ( lastVisiblePosition == blackNumberInfos.size() - 1 ) {
                            if ( lastVisiblePosition == totalNumber - 1 ) {
//                                System.out.println("已经到最后一条数据了");
                                ToastUtils.showShortToast(CallSafeActivity2.this, "已经到最后一条数据了");
                            } else {
                                mStartIndex = mStartIndex + mMaxCount;
//                                System.out.println("起始位置为：" + mStartIndex);
                                ll_show.setVisibility(View.VISIBLE);
                                initData();
                            }
                        }

                        break;
                }
            }

            /**
             * 改变时调用
             * @param view
             * @param firstVisibleItem
             * @param visibleItemCount
             * @param totalItemCount
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                System.out.println("onScroll");
            }
        });
    }

    /**
     * 添加黑名单功能
     *
     * @param v
     */
    public void addBlackNumber(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CallSafeActivity2.this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(CallSafeActivity2.this, R.layout.dialog_add_black_number, null);
        dialog.setView(view);
        final EditText et_add_black_number = (EditText) view.findViewById(R.id.et_add_black_number);
        final CheckBox cb_phone = (CheckBox) view.findViewById(R.id.cb_phone);
        final CheckBox cb_sms = (CheckBox) view.findViewById(R.id.cb_sms);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_black_number = et_add_black_number.getText().toString().trim();
                if ( TextUtils.isEmpty(str_black_number) ) {
                    ToastUtils.showShortToast(CallSafeActivity2.this, "请输入需要拦截的电话号码");
                    return;
                }
                String mode;
                if ( cb_phone.isChecked() && cb_sms.isChecked() ) {
                    mode = "3";
                } else if ( cb_phone.isChecked() ) {
                    mode = "1";
                } else if ( cb_sms.isChecked() ) {
                    mode = "2";
                } else {
                    ToastUtils.showShortToast(CallSafeActivity2.this, "请勾选拦截模式");
                    return;
                }
                boolean result = dao.add(str_black_number, mode);
                if ( result ) {
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.setNumber(str_black_number);
                    blackNumberInfo.setMode(mode);
                    blackNumberInfos.add(0, blackNumberInfo);
                    if ( adapter == null ) {
                        adapter = new BlackNumberAdapter(blackNumberInfos, CallSafeActivity2.this);
                        list_view.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    ToastUtils.showShortToast(CallSafeActivity2.this, "添加数据失败");
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
