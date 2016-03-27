package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {

	private GestureDetector mGesture;
	public  SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		mGesture = new GestureDetector(this, new SimpleOnGestureListener() {

			/*
			 * 监听手势滑动 e1表示滑动的起点，e2表示滑动终点 velocityX表示水平速度 velocityY表示垂直速度
			 */
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if (Math.abs(e1.getRawY() - e2.getRawY()) > 100) {
					Toast.makeText(BaseSetupActivity.this, "不明确的手势操作",
							Toast.LENGTH_SHORT).show();
					return true;
				}
				
				if (Math.abs(velocityX)<100) {
					Toast.makeText(BaseSetupActivity.this, "滑动太慢了",
							Toast.LENGTH_SHORT).show();
					return true;
				}

				// 向左滑动，实现下一页功能
				if (e1.getRawX() - e2.getRawX() > 200) {
					goNextPage();
					return true;
				}

				// 向右滑动，实现上一页功能
				if (e2.getRawX() - e1.getRawX() > 200) {
					goPreviousPage();
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	protected abstract void goPreviousPage();

	protected abstract void goNextPage();

	// 点击下一页按钮
	public void next(View v) {
		goNextPage();
	}

	// 点击上一页按钮
	public void previous(View v) {
		goPreviousPage();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGesture.onTouchEvent(event); // 委托手势操作处理触摸事件
		return super.onTouchEvent(event);
	}

}
