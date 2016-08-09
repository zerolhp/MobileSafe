package com.lhp.mobilesafe.activity;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.lhp.mobilesafe.R;

public class Setup1Activity extends BaseSetupActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	
	@Override
	protected void showNextPage() {
		Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
		startActivity(intent);
		finish();
		// 界面切换时开启平移动画效果
		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
	}

	@Override
	protected void showPrePage() { }
	
}
