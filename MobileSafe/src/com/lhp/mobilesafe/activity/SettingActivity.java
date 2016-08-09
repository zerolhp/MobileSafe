package com.lhp.mobilesafe.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.service.BlackNumberService;
import com.lhp.mobilesafe.utils.ConstantValue;
import com.lhp.mobilesafe.utils.SpUtil;
import com.lhp.mobilesafe.view.SettingItemView;

public class SettingActivity extends BaseActivity {

	private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		tv_title = (TextView) findViewById(R.id.tv_title);
		AssetManager mAssetManager = getAssets();
		Typeface tf = Typeface.createFromAsset(mAssetManager,
				"fonts/huawenxinkai.ttf");
		tv_title.setTypeface(tf);

		initUpdateView();
		initPhoneAddressView();
		initBlackNumber();
	}

	private void initUpdateView() {
		final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
		siv_update.tv_title.setText("自动更新设置");
		siv_update.mDeson = "自动更新已开启";
		siv_update.mDesoff = "自动更新已关闭";
		// 获取已有的开关状态
		boolean OPEN_UPDATE = SpUtil.getBoolean(this,
				ConstantValue.OPEN_UPDATE, false);
		// 根据上一次存储的结果去做决定
		siv_update.setCheck(OPEN_UPDATE);
		if (OPEN_UPDATE) {
			siv_update.rootView.setBackgroundColor(Color.parseColor("#666666"));
		}
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				siv_update.setCheck(!siv_update.cb_box.isChecked());
				SpUtil.putBoolean(getApplicationContext(),
						ConstantValue.OPEN_UPDATE,
						siv_update.cb_box.isChecked());
				if (siv_update.cb_box.isChecked()) {
					siv_update.rootView.setBackgroundColor(Color
							.parseColor("#666666"));
				} else {
					siv_update.rootView.setBackgroundColor(Color
							.parseColor("#cccccc"));
				}
			}
		});
	}

	private void initPhoneAddressView() {
		final SettingItemView siv_phoneAddress = (SettingItemView) findViewById(R.id.siv_phoneAddress);
		siv_phoneAddress.tv_title.setText("号码归属地显示设置");
		siv_phoneAddress.mDeson = "归属地显示已开启";
		siv_phoneAddress.mDesoff = "归属地显示已关闭";
		// 获取已有的开关状态
		boolean SHOW_PHONE_ADDRESS = SpUtil.getBoolean(this,
				ConstantValue.SHOW_PHONE_ADDRESS, true);
		// 根据上一次存储的结果去做决定
		siv_phoneAddress.setCheck(SHOW_PHONE_ADDRESS);
		if (SHOW_PHONE_ADDRESS) {
			siv_phoneAddress.rootView.setBackgroundColor(Color
					.parseColor("#666666"));
		}
		siv_phoneAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				siv_phoneAddress.setCheck(!siv_phoneAddress.cb_box.isChecked());
				SpUtil.putBoolean(getApplicationContext(),
						ConstantValue.SHOW_PHONE_ADDRESS,
						siv_phoneAddress.cb_box.isChecked());
				if (siv_phoneAddress.cb_box.isChecked()) {
					siv_phoneAddress.rootView.setBackgroundColor(Color
							.parseColor("#666666"));
				} else {
					siv_phoneAddress.rootView.setBackgroundColor(Color
							.parseColor("#cccccc"));
				}
			}
		});
	}

	private void initBlackNumber() {
		final SettingItemView siv_blackNumber = (SettingItemView) findViewById(R.id.siv_blackNumber);
		siv_blackNumber.tv_title.setText("黑名单设置");
		siv_blackNumber.mDeson = "黑名单功能已开启";
		siv_blackNumber.mDesoff = "黑名单功能已关闭";
		// 获取已有的开关状态
		boolean OPEN_BLACKNUMBER = SpUtil.getBoolean(this,
				ConstantValue.OPEN_BLACKNUMBER, true);
		// 根据上一次存储的结果去做决定
		siv_blackNumber.setCheck(OPEN_BLACKNUMBER);
		if (OPEN_BLACKNUMBER) {
			siv_blackNumber.rootView.setBackgroundColor(Color
					.parseColor("#666666"));
		}
		siv_blackNumber.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				siv_blackNumber.setCheck(!siv_blackNumber.cb_box.isChecked());
				SpUtil.putBoolean(getApplicationContext(),
						ConstantValue.SHOW_PHONE_ADDRESS,
						siv_blackNumber.cb_box.isChecked());
				if (siv_blackNumber.cb_box.isChecked()) {
					startService(new Intent(getApplicationContext(),
							BlackNumberService.class));
					siv_blackNumber.rootView.setBackgroundColor(Color
							.parseColor("#666666"));
				} else {
					stopService(new Intent(getApplicationContext(),
							BlackNumberService.class));
					siv_blackNumber.rootView.setBackgroundColor(Color
							.parseColor("#cccccc"));
				}
			}
		});
	}

}
