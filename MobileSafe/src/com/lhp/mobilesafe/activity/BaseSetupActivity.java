package com.lhp.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends BaseActivity {

	private GestureDetector gestureDetector; // 手势检测器

	/** 在onCeate()中实现滑动监听 **/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					/** 滑动手势回调 **/
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if (e1.getX() - e2.getX() > 20) { // 根据DOWN和UP的X坐标决定左滑还是右滑
							showNextPage();
						}
						if (e1.getX() - e2.getX() < -20){
							showPrePage();
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});
	}

	/** 重写Activity的onTouchEvent()：由GestureDetector的onTouchEvent()来具体处理触摸事件。　**/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event); // 由GestureDetector来处理触摸事件
		return super.onTouchEvent(event);
	}

	/** 抽象方法 **/
	protected abstract void showNextPage();
	protected abstract void showPrePage();

}
