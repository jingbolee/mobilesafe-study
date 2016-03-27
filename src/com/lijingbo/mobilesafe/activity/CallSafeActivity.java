package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;
import com.lijingbo.mobilesafe.adapter.BlackNumberAdapter;
import com.lijingbo.mobilesafe.bean.BlackNumberInfo;
import com.lijingbo.mobilesafe.db.dao.BlackNumberDao;
import com.lijingbo.mobilesafe.utils.ToastUtils;

import java.util.List;

/**
 * 分页加载数据
 */
public class CallSafeActivity extends Activity {

    private ListView list_view;
    private LinearLayout ll_show;
    private EditText et_pageNumber;
    private TextView tv_pageNumber;

    private BlackNumberDao dao;
    private List< BlackNumberInfo > blackNumberInfos;

    private int mPageSize = 20;
    private int mCurrentPage = 0;
    private int mTotalPage;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ll_show.setVisibility(View.INVISIBLE);
            tv_pageNumber.setText((mCurrentPage + 1) + "/" + (mTotalPage + 1));
            adapter = new BlackNumberAdapter(blackNumberInfos, CallSafeActivity.this);
            list_view.setAdapter(adapter);
        }
    };
    private BlackNumberAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);

        initData();
        initUI();


    }

    private void initData() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                SystemClock.sleep(3000);
                dao = new BlackNumberDao(CallSafeActivity.this);
                blackNumberInfos = dao.findPage(mCurrentPage, mPageSize);

                int totalNumber = dao.getCount();
//                System.out.println("总共含有：" + totalNumber);
                if ( totalNumber != -1 ) {
                    mTotalPage = totalNumber / mPageSize;
//                    System.out.println("总页码：" + mTotalPage);
                } else {
                    ToastUtils.showShortToast(CallSafeActivity.this, "无法获取到含有多少数据");
                }
                handler.sendEmptyMessage(0);
            }
        }).start();

    }


    private void initUI() {
        list_view = (ListView) findViewById(R.id.list_view);
        ll_show = (LinearLayout) findViewById(R.id.ll_show);
        et_pageNumber = (EditText) findViewById(R.id.et_page_number);
        tv_pageNumber = (TextView) findViewById(R.id.tv_page_number);

    }

    private int getUserPageNumber() {
        String str_page_number = et_pageNumber.getText().toString().trim();

        if ( !TextUtils.isEmpty(str_page_number) ) {
            int i = Integer.parseInt(str_page_number);
            if ( i > 0 ) {
                mCurrentPage = i - 1;
                return i - 1;
            }
        }
        return -1;
    }

    public void prePage(View v) {
        if ( mCurrentPage <= 0 ) {
            ToastUtils.showShortToast(this, "已经是第一页了");
        } else {
            ll_show.setVisibility(View.VISIBLE);
            mCurrentPage = mCurrentPage - 1;
            initData();
        }

    }

    public void nextPage(View v) {
        if ( mCurrentPage >= mTotalPage ) {
            ToastUtils.showShortToast(this, "已经是最后一页了");
        } else {
            mCurrentPage = mCurrentPage + 1;
            ll_show.setVisibility(View.VISIBLE);
            initData();
        }
    }

    public void jumpPage(View v) {
        int number = getUserPageNumber();
        if ( number < 0 || number > mTotalPage ) {
            ToastUtils.showShortToast(this, "请输入正确的页面");
        } else {
            mCurrentPage = number;
            ll_show.setVisibility(View.VISIBLE);
            initData();
        }
    }

}
