package com.lhp.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.db.dao.VirusDao;
import com.lhp.mobilesafe.utils.Md5Util;

public class AnitVirusActivity extends BaseActivity {

	protected static final int SCANING = 100;
	protected static final int SCAN_FINISH = 101;

	private ImageView iv_scanning;
	private TextView tv_name;
	private ProgressBar pb_bar;
	private LinearLayout ll_add_text;
	private int index = 0;
	private List<ScanInfo> mVirusScanInfoList;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANING:
				ScanInfo info = (ScanInfo) msg.obj;
				tv_name.setText(info.name);
				TextView textView = new TextView(getApplicationContext());
				if (info.isVirus) {
					textView.setTextColor(Color.RED);
					textView.setTextSize(11);
					textView.setText(" 发现病毒：" + info.name);
				} else {
					textView.setTextColor(Color.parseColor("#3d85c6"));
					textView.setTextSize(11);
					textView.setText(" 扫描安全：" + info.name);
				}
				ll_add_text.addView(textView, 0);
				break;
			case SCAN_FINISH:
				tv_name.setText("扫描完成!");
				iv_scanning.clearAnimation();
				unInstallVirus();
				showDialog();
				break;
			}
		};
	};
	
	class ScanInfo {
		public boolean isVirus;
		public String packageName;
		public String name;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anit_virus);

		initView();
		initAnimation();
		checkVirus();
	}

	private void initView() {
		TextView tv_title = (TextView)findViewById(R.id.tv_title);
		AssetManager mAssetManager = getAssets();
		Typeface tf = Typeface.createFromAsset(mAssetManager, "fonts/huawenxinkai.ttf");
		tv_title.setTypeface(tf);
		
		iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
		tv_name = (TextView) findViewById(R.id.tv_name);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
	}
	
	protected void unInstallVirus() {
		for (ScanInfo scanInfo : mVirusScanInfoList) {
			String packageName = scanInfo.packageName;
			Intent intent = new Intent("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:" + packageName));
			startActivity(intent);
		}
	}

	private void checkVirus() {
		new Thread() {
			private ScanInfo scanInfo;

			public void run() {
				// 获取数据库中所有的病毒程序的签名文件的MD5码
				List<String> virusList = VirusDao.getVirusList();
				// 获取手机上面的所有应用程序的签名文件的MD5码
				PackageManager pm = getPackageManager();
				List<PackageInfo> packageInfoList = pm
						.getInstalledPackages(PackageManager.GET_SIGNATURES
								+ PackageManager.GET_UNINSTALLED_PACKAGES);
				mVirusScanInfoList = new ArrayList<ScanInfo>();
				List<ScanInfo> scanInfoList = new ArrayList<ScanInfo>();

				// 设置进度条的最大值
				pb_bar.setMax(packageInfoList.size());

				for (PackageInfo packageInfo : packageInfoList) {
					scanInfo = new ScanInfo();
					// 获取本机所有应用程序的签名文件的数组
					Signature[] signatures = packageInfo.signatures;
					// 获取签名文件数组的第一位,然后进行md5,将此md5和数据库中的md5比对
					Signature signature = signatures[0];
					String string = signature.toCharsString();
					// 32位字符串,16进制字符(0-f)
					String encoder = Md5Util.encoder(string);
					// 病毒库中是否包含此MD5码
					if (virusList.contains(encoder)) {
						// 记录病毒
						scanInfo.isVirus = true;
						mVirusScanInfoList.add(scanInfo);
					} else {
						scanInfo.isVirus = false;
					}
					// 维护对象的包名和应用名称
					scanInfo.packageName = packageInfo.packageName;
					scanInfo.name = packageInfo.applicationInfo.loadLabel(pm)
							.toString();
					scanInfoList.add(scanInfo);
					// 更新进度条
					index++;
					pb_bar.setProgress(index);

					try {
						Thread.sleep(50 + new Random().nextInt(100));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// 8.在子线程中发送消息,告知主线程更新UI(1:顶部扫描应用的名称2:扫描过程中往线性布局中添加view)
					Message msg = Message.obtain();
					msg.what = SCANING;
					msg.obj = scanInfo;
					mHandler.sendMessage(msg);
				}
				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();
	}

	private void initAnimation() {
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setDuration(900);
		rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
		rotateAnimation.setFillAfter(true);
		iv_scanning.startAnimation(rotateAnimation);
	}

	private void showDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		final View view = View.inflate(this, R.layout.dialog_shadu, null);
		dialog.setView(view);
		dialog.show();

		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				startActivity(new Intent(getApplicationContext(),
						HomeActivity.class));
			}
		});

	}
	
}
