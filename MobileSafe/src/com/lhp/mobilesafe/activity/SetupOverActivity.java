package com.lhp.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.utils.ConstantValue;
import com.lhp.mobilesafe.utils.SpUtil;

public class SetupOverActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
		if(setup_over){
			setContentView(R.layout.activity_setup_over);
		}else{
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}
	}	
}
