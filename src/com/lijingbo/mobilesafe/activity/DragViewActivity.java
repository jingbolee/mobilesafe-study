package com.lijingbo.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lijingbo.mobilesafe.R;

public class DragViewActivity extends Activity {

	private ImageView ivDrag;
	private SharedPreferences mPref;
	private TextView tvTop;
	private TextView tvBottom;
	long[] mHits = new long[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		int lastTop = mPref.getInt("lastTop", 0);
		int lastLeft = mPref.getInt("lastLeft", 0);

		ivDrag = (ImageView) findViewById(R.id.iv_drag);
		tvTop = (TextView) findViewById(R.id.tv_top);
		tvBottom = (TextView) findViewById(R.id.tv_bottom);

		// 获取屏幕的宽和高
		final int screenWidth = getWindowManager().getDefaultDisplay()
				.getWidth();
		final int screenHeight = getWindowManager().getDefaultDisplay()
				.getHeight();

		// 根据最后一次控件离上边距的距离，判断是显示顶部的提示文字还是底部的提示文字
		if (lastTop > screenHeight / 2) {
			tvTop.setVisibility(View.VISIBLE);
			tvBottom.setVisibility(View.INVISIBLE);
		} else {
			tvTop.setVisibility(View.INVISIBLE);
			tvBottom.setVisibility(View.VISIBLE);
		}

		// 根据保存的控件上边距和左边距，来绘制该控件在父类布局上的位置
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivDrag
				.getLayoutParams();
		layoutParams.leftMargin = lastLeft;
		layoutParams.topMargin = lastTop;
		ivDrag.setLayoutParams(layoutParams);

		ivDrag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				// 给数组最后一个位置赋值
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				// 判断数组第一个位置的时间与当前时间的查是否小于500毫秒，假如小于的话，就是执行了多次点击事件。
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					ivDrag.layout(screenWidth / 2 - ivDrag.getWidth() / 2,
							ivDrag.getTop(),
							screenWidth / 2 + ivDrag.getWidth() / 2,
							ivDrag.getBottom());
				}
			}
		});

		// 给控件设置点击事件
		ivDrag.setOnTouchListener(new OnTouchListener() {

			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					// 计算偏移量
					int dX = endX - startX;
					int dY = endY - startY;

					// 计算出移动会控件与父控件的各个边距
					int l = ivDrag.getLeft() + dX;
					int t = ivDrag.getTop() + dY;
					int r = ivDrag.getRight() + dX;
					int b = ivDrag.getBottom() + dY;

					// 判断控件移动的位置，假如左边距出了屏幕或者上边距出了屏幕或者右边距出了屏幕或者低边距出了屏幕，就不绘制了。
					if (l < 0 || t < 0 || r > screenWidth
							|| b > screenHeight - 50) {
						break;
					}

					// 根据控件上边距移动的位置与屏幕的1/2宽度进行比较，来判断是否显示顶部的提示文字还是底部的提示文字
					if (t > screenHeight / 2) {
						tvTop.setVisibility(View.VISIBLE);
						tvBottom.setVisibility(View.INVISIBLE);
					} else {
						tvTop.setVisibility(View.INVISIBLE);
						tvBottom.setVisibility(View.VISIBLE);
					}

					// 根据四个边距重新绘制控件
					ivDrag.layout(l, t, r, b);

					// 重新设置起点坐标的X和Y值
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:

					// 记录最后离开屏幕的坐标
					mPref.edit().putInt("lastLeft", ivDrag.getLeft()).commit();
					mPref.edit().putInt("lastTop", ivDrag.getTop()).commit();
					break;

				default:
					break;
				}

				return false; 
			}
		});
	}

}
