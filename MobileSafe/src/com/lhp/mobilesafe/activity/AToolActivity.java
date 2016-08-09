package com.lhp.mobilesafe.activity;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.engine.SMSBackup;
import com.lhp.mobilesafe.engine.SMSBackup.CallBack;

public class AToolActivity extends BaseActivity {

	private Button btn_commonPhoneNumber;
	private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);

		tv_title = (TextView) findViewById(R.id.tv_title);
		AssetManager mAssetManager = getAssets();
		Typeface tf = Typeface.createFromAsset(mAssetManager,
				"fonts/huawenxinkai.ttf");
		tv_title.setTypeface(tf);
		
		initSMSBackup();
		initCommonPhoneNumber();
		initResetPsd();
	}

	private void initSMSBackup() {
		Button btn_sms_backup = (Button) findViewById(R.id.btn_sms_backup);
		btn_sms_backup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final ProgressDialog progressDialog = new ProgressDialog(
						AToolActivity.this);
				progressDialog.setIcon(R.drawable.suspending_view);
				progressDialog.setTitle("短信备份中...");
				progressDialog
						.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.show();

				new Thread() {
					@Override
					public void run() {
						String path = Environment.getExternalStorageDirectory()
								.getAbsolutePath()
								+ File.separator
								+ "sms.xml";
						SMSBackup.backup(getApplicationContext(), path,
								new CallBack() {
									@Override
									public void setProgress(int index) {
										progressDialog.setProgress(index);
									}

									@Override
									public void setMax(int max) {
										progressDialog.setMax(max);
									}
								});

						progressDialog.dismiss();
					}
				}.start();
			}
		});
	}
	
	private void initCommonPhoneNumber() {
		btn_commonPhoneNumber = (Button) findViewById(R.id.btn_commonPhoneNumber);
		btn_commonPhoneNumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), CommonNumberQueryActivity.class));
			};
		});
	}

	private void initResetPsd() {
		Button btn_resetPsd = (Button) findViewById(R.id.btn_resetPsd);
		btn_resetPsd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), Setup1Activity.class));
			}
		});
		
	}
}
