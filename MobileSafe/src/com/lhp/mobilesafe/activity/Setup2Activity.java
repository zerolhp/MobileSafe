package com.lhp.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.utils.ConstantValue;
import com.lhp.mobilesafe.utils.SpUtil;
import com.lhp.mobilesafe.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView siv_sim_bound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		initUI();
	}

	private void initUI() {
		siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
		siv_sim_bound.tv_title.setText("点击绑定SIM卡");
		siv_sim_bound.mDeson = "绑定SIM卡已开启";
		siv_sim_bound.mDesoff = "绑定SIM卡已关闭";
		siv_sim_bound.setCheck(false);
		siv_sim_bound.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				siv_sim_bound.setCheck(!siv_sim_bound.cb_box.isChecked());
				if (siv_sim_bound.cb_box.isChecked()) {
					// 通过SystemServer的TelephonyManager获取SIM卡序列号
					TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String simSerialNumber = manager.getSimSerialNumber();
					// 存储
					SpUtil.putString(getApplicationContext(),
							ConstantValue.SIM_NUMBER, simSerialNumber);
					siv_sim_bound.rootView.setBackgroundColor(Color
							.parseColor("#666666"));
				} else {

					// 将SIM卡序列号在SP中删除
					SpUtil.remove(getApplicationContext(),
							ConstantValue.SIM_NUMBER);
					siv_sim_bound.rootView.setBackgroundColor(Color
							.parseColor("#cccccc"));
				}
			}
		});
	}

	@Override
	protected void showNextPage() {
		String serialNumber = SpUtil.getString(this, ConstantValue.SIM_NUMBER,
				"");
		if (!TextUtils.isEmpty(serialNumber)) {
			Intent intent = new Intent(getApplicationContext(),
					Setup3Activity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			Toast.makeText(getApplicationContext(), "请绑定sim卡",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void showPrePage() {
		Intent intent = new Intent(getApplicationContext(),
				Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}

}
