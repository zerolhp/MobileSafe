package com.lhp.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.utils.ConstantValue;
import com.lhp.mobilesafe.utils.SpUtil;

public class Setup3Activity extends BaseSetupActivity {

	private EditText et_phone_number;
	private Button bt_select_number;
	private Button bt_finish;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		initUI();
	}

	private void initUI() {
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		bt_select_number = (Button) findViewById(R.id.bt_select_number);
		bt_finish = (Button) findViewById(R.id.bt_finish);
		bt_finish.setVisibility(View.GONE);

		bt_select_number.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						ContactListActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	protected void showNextPage() {
	}

	@Override
	protected void showPrePage() {
		Intent intent = new Intent(getApplicationContext(),
				Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}

	/** 回显点击的号码 **/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			String number = data.getStringExtra("number");
			number = number.replace("-", "").replace(" ", "").trim();
			et_phone_number.setText(number);
			SpUtil.putString(getApplicationContext(),
					ConstantValue.CONTACT_PHONE, number);
			bt_finish.setVisibility(View.VISIBLE);
			bt_finish.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(getApplicationContext(),
							HomeActivity.class));
				}
			});
		}
		;
		super.onActivityResult(requestCode, resultCode, data);
	}

}
