package com.lhp.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.activity.HomeActivity;
import com.lhp.mobilesafe.utils.ConstantValue;
import com.lhp.mobilesafe.utils.SpUtil;

public class SuspendingViewService extends Service {

	private WindowManager mWM;
	private int mScreenWidth;
	private int mScreenHeight;
	private WindowManager.LayoutParams params;
	private View mSuspendingView;
	private int startY;

	private long startTime = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		initView();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void initView() {
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.gravity = Gravity.TOP + Gravity.LEFT;
		params.x = mScreenWidth;
		params.y = SpUtil.getInt(getApplicationContext(),
				ConstantValue.PARAMS_Y, 650);

		mSuspendingView = View.inflate(this, R.layout.suspending_view, null);
		ImageView iv_suspendingView = (ImageView) mSuspendingView
				.findViewById(R.id.iv_suspendingView);
		RotateAnimation animRotate = new RotateAnimation(0, 359,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animRotate.setDuration(8000);
		animRotate.setRepeatCount(-1);
		iv_suspendingView.startAnimation(animRotate);

		iv_suspendingView.setOnTouchListener(new OnTouchListener() {
			private int startX;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();

					int disX = moveX - startX;
					int disY = moveY - startY;

					params.x = params.x + disX;
					params.y = params.y + disY;

					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}

					if (params.x > mScreenWidth - mSuspendingView.getWidth()) {
						params.x = mScreenWidth - mSuspendingView.getWidth();
					}

					if (params.y > mScreenHeight - 22
							- mSuspendingView.getHeight()) {
						params.y = mScreenHeight - 22
								- mSuspendingView.getHeight();
					}

					mWM.updateViewLayout(mSuspendingView, params);

					// 在第一次移动完成后,将最终坐标作为第二次移动的起始坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					// 手指放开的时候,如果放手坐标,则指定区域内
					if (startX < mScreenWidth / 2) {
						params.x = 0;
					} else {
						params.x = mScreenWidth;
					}
					mWM.updateViewLayout(mSuspendingView, params);
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.PARAMS_Y, startY);
					break;
				}
				return false;
			}
		});

		iv_suspendingView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (startTime != 0) {
					long endTime = System.currentTimeMillis();
					if (endTime - startTime < 500) {
						Intent intent = new Intent(getApplicationContext(),
								HomeActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				}
				startTime = System.currentTimeMillis();
			}
		});

		mWM.addView(mSuspendingView, params);
	}

	@Override
	public void onDestroy() {
		if (mWM != null && mSuspendingView != null) {
			mWM.removeView(mSuspendingView);
		}
		super.onDestroy();
	}

}
