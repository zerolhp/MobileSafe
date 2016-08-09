package com.lhp.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.db.bean.AppInfo;
import com.lhp.mobilesafe.engine.AppInfoProvider;

public class AppManagerActivity extends BaseActivity implements OnClickListener {

	private ProgressBar pb;
	private Button btn_myApp;
	private Button btn_sysApp;
	private ListView lv_app_list;
	private PopupWindow mPopupWindow;
	private List<AppInfo> mAppInfoList;
	private List<AppInfo> mSystemList;
	private List<AppInfo> mCustomerList;
	private AppInfo mAppInfo;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				initMyAppList();
				pb.setVisibility(View.INVISIBLE);
				break;
			case 2:
				setClickable();
				break;
			default:
				break;
			}
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		
		TextView tv_title = (TextView)findViewById(R.id.tv_title);
		pb = (ProgressBar) findViewById(R.id.pb);
		
		AssetManager mAssetManager = getAssets();
		Typeface tf = Typeface.createFromAsset(mAssetManager, "fonts/huawenxinkai.ttf");
		
		tv_title.setTypeface(tf);
		
		initTitle();
		initList();
	}

	private void initTitle() {
		String path = Environment.getDataDirectory().getAbsolutePath();
		String sdPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String memoryAvailSpace = Formatter.formatFileSize(this,
				getAvailSpace(path));
		String sdMemoryAvailSpace = Formatter.formatFileSize(this,
				getAvailSpace(sdPath));

		TextView tv_memory = (TextView) findViewById(R.id.tv_memory);
		TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);

		tv_memory.setText("内存可用:" + memoryAvailSpace);
		tv_sd_memory.setText("sd卡可用:" + sdMemoryAvailSpace);
	}

	private void initList() {
		lv_app_list = (ListView) findViewById(R.id.lv_app_list);
		btn_myApp = (Button) findViewById(R.id.btn_myApp);
		btn_sysApp = (Button) findViewById(R.id.btn_sysApp);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					mHandler.sendEmptyMessage(2);
				}
			};
		}.start();
		getData();
	}

	private void setClickable() {
		btn_myApp.setOnClickListener(this);
		btn_sysApp.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_myApp:
			initMyAppList();
			break;
		case R.id.btn_sysApp:
			initSysAppList();
			break;
		case R.id.tv_uninstall:
			if (mAppInfo.isSystem) {
				Toast.makeText(getApplicationContext(), "系统应用不可卸载",
						Toast.LENGTH_LONG).show();
			} else {
				Intent intent = new Intent("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:" + mAppInfo.getPackageName()));
				startActivity(intent);
			}
			break;
		case R.id.tv_start:
			// 通过桌面去启动指定包名应用
			PackageManager pm = getPackageManager();
			// 通过Launch开启制定包名的意图,去开启应用
			Intent launchIntentForPackage = pm
					.getLaunchIntentForPackage(mAppInfo.getPackageName());
			if (launchIntentForPackage != null) {
				startActivity(launchIntentForPackage);
			} else {
				Toast.makeText(getApplicationContext(), "此应用不能被开启",
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.tv_share:
			// 通过短信应用,向外发送短信
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT,
					"分享一个应用,应用名称为" + mAppInfo.getName());
			intent.setType("text/plain");
			startActivity(intent);
			break;
		default:
			break;
		}
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}

	private long getAvailSpace(String path) {
		// 获取可用磁盘大小类
		StatFs statFs = new StatFs(path);
		// 获取可用区块的个数
		long count = statFs.getAvailableBlocks();
		// 获取区块的大小
		long size = statFs.getBlockSize();
		// 区块大小*可用区块个数 == 可用空间大小
		return count * size;
	}

	private void initMyAppList() {
		btn_myApp.setBackgroundResource(R.drawable.btn_bg_blue);
		btn_myApp.setTextColor(Color.parseColor("#FFFFFF"));
		btn_sysApp.setBackgroundColor(Color.parseColor("#FFFFFF"));
		btn_sysApp.setTextColor(Color.parseColor("#000000"));
		
		MyAppAdapter myAppAdapter = new MyAppAdapter();
		lv_app_list.setAdapter(myAppAdapter);
		lv_app_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				showPopupWindow(view);
				mAppInfo = mCustomerList.get(position);
			}
		});
	}

	private void initSysAppList() {
		btn_sysApp.setBackgroundResource(R.drawable.btn_bg_blue);
		btn_sysApp.setTextColor(Color.parseColor("#FFFFFF"));
		btn_myApp.setBackgroundColor(Color.parseColor("#FFFFFF"));
		btn_myApp.setTextColor(Color.parseColor("#000000"));
		
		SysAppAdapter sysAppAdapter = new SysAppAdapter();
		lv_app_list.setAdapter(sysAppAdapter);
		lv_app_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				showPopupWindow(view);
				mAppInfo = mSystemList.get(position);
			}
		});
	}

	protected void showPopupWindow(View view) {
		View popupView = View.inflate(this, R.layout.popupwindow_layout, null);

		TextView tv_uninstall = (TextView) popupView
				.findViewById(R.id.tv_uninstall);
		TextView tv_start = (TextView) popupView.findViewById(R.id.tv_start);
		TextView tv_share = (TextView) popupView.findViewById(R.id.tv_share);

		tv_uninstall.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		tv_share.setOnClickListener(this);

		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(1000);
		alphaAnimation.setFillAfter(true);

		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleAnimation.setDuration(1000);
		alphaAnimation.setFillAfter(true);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);

		mPopupWindow = new PopupWindow(popupView,
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, true);
		// 透明背景
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
		// 指定窗体位置
		mPopupWindow.showAsDropDown(view, 600, -view.getHeight() - 12);
		// 添加执行动画
		popupView.startAnimation(animationSet);
	}

	@Override
	protected void onResume() {
		// 重新获取数据
		getData();
		super.onResume();
	}

	private void getData() {
		new Thread() {
			public void run() {
				mAppInfoList = AppInfoProvider
						.getAppInfoList(getApplicationContext());
				mSystemList = new ArrayList<AppInfo>();
				mCustomerList = new ArrayList<AppInfo>();
				for (AppInfo appInfo : mAppInfoList) {
					if (appInfo.isSystem) {
						mSystemList.add(appInfo);
					} else {
						mCustomerList.add(appInfo);
					}
				}
				mHandler.sendEmptyMessage(1);
			};
		}.start();
	}

	class MyAppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mCustomerList.size();
		}

		@Override
		public AppInfo getItem(int position) {
			return mCustomerList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyAppViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.listview_app_item, null);
				holder = new MyAppViewHolder();
				holder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			} else {
				holder = (MyAppViewHolder) convertView.getTag();
			}
			holder.iv_icon
					.setBackgroundDrawable(mCustomerList.get(position).icon);
			holder.tv_name.setText(mCustomerList.get(position).name);
			return convertView;
		}
	}

	class SysAppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mSystemList.size();
		}

		@Override
		public AppInfo getItem(int position) {
			return mSystemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SysAppViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.listview_app_item, null);
				holder = new SysAppViewHolder();
				holder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			} else {
				holder = (SysAppViewHolder) convertView.getTag();
			}
			holder.iv_icon
					.setBackgroundDrawable(mSystemList.get(position).icon);
			holder.tv_name.setText(mSystemList.get(position).name);
			return convertView;
		}
	}

	static class MyAppViewHolder {
		ImageView iv_icon;
		TextView tv_name;
	}

	static class SysAppViewHolder {
		ImageView iv_icon;
		TextView tv_name;
	}

}
