package com.lhp.mobilesafe.activity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.utils.ConstantValue;
import com.lhp.mobilesafe.utils.Md5Util;
import com.lhp.mobilesafe.utils.SpUtil;

public class CacheClearActivity extends BaseActivity {

	protected static final int SHOW_CACHE = 100;
	protected static final int CHECK_CACHE_APP = 101;
	protected static final int CHECK_FINISH = 102;
	protected static final int CLEAR_ALL_CACHE = 103;
	protected static final String tag = "CacheClearActivity";

	private ProgressBar pb_bar;
	private TextView tv_name;
	private TextView tv_title;
	private LinearLayout ll_add_text;
	private PackageManager mPm;
	private int mIndex = 0;
	long totalCache = 0;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_CACHE:
				View view = View.inflate(getApplicationContext(),
						R.layout.linearlayout_cache_item, null);
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_item_name = (TextView) view
						.findViewById(R.id.tv_name);
				TextView tv_memory_info = (TextView) view
						.findViewById(R.id.tv_memory_info);
				final CacheInfo cacheInfo = (CacheInfo) msg.obj;
				iv_icon.setBackgroundDrawable(cacheInfo.icon);
				tv_item_name.setText(cacheInfo.name);
				tv_memory_info.setText(Formatter.formatFileSize(
						getApplicationContext(), cacheInfo.cacheSize));
				ll_add_text.addView(view, 0);
				break;
			case CHECK_FINISH:
				tv_name.setText("扫描完成!");
				clearCache();
				break;
			case CLEAR_ALL_CACHE:
				ll_add_text.removeAllViews();
				showDialog();
				break;
			}
		};
	};

	class CacheInfo {
		public String name;
		public Drawable icon;
		public String packagename;
		public long cacheSize;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clear);
		initView();
		initData();
	}
	
	private void initView() {

		tv_title = (TextView) findViewById(R.id.tv_title);
		AssetManager mAssetManager = getAssets();
		Typeface tf = Typeface.createFromAsset(mAssetManager,
				"fonts/huawenxinkai.ttf");
		tv_title.setTypeface(tf);

		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		tv_name = (TextView) findViewById(R.id.tv_name);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
	}

	private void initData() {
		// 遍历手机中的
		new Thread() {
			public void run() {
				// 获取包管理者对象
				mPm = getPackageManager();
				// 获取安装在手机上的所有应用
				List<PackageInfo> installedPackages = mPm
						.getInstalledPackages(0);
				// 设置进度条的最大值(应用总数)
				pb_bar.setMax(installedPackages.size());
				// 遍历每一个应用,获取有缓存的应用信息(应用名称,图标,缓存大小,包名)
				for (PackageInfo packageInfo : installedPackages) {
					// 包名作为获取缓存信息的条件
					String packageName = packageInfo.packageName;
					getPackageCache(packageName);
					try {
						Thread.sleep(100 + new Random().nextInt(50));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mIndex++;
					pb_bar.setProgress(mIndex);
				}
				Message msg = Message.obtain();
				msg.what = CHECK_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();
	}

	// 通过包名获取该应用的缓存信息
	protected void getPackageCache(String packageName) {
		IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
			public void onGetStatsCompleted(PackageStats stats,
					boolean succeeded) {
				long cacheSize = stats.cacheSize;
				totalCache += cacheSize;
				if (cacheSize > 12 * 1024) {
					Message msg = Message.obtain();
					msg.what = SHOW_CACHE;
					CacheInfo cacheInfo = null;
					try {
						cacheInfo = new CacheInfo();
						cacheInfo.cacheSize = cacheSize;
						cacheInfo.packagename = stats.packageName;
						cacheInfo.name = mPm
								.getApplicationInfo(stats.packageName, 0)
								.loadLabel(mPm).toString();
						cacheInfo.icon = mPm.getApplicationInfo(
								stats.packageName, 0).loadIcon(mPm);
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					msg.obj = cacheInfo;
					mHandler.sendMessage(msg);
				}
			}
		};

		try {
			// 获取指定类的字节码文件
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			// 获取调用方法的对象
			Method method = clazz.getMethod("getPackageSizeInfo", String.class,
					IPackageStatsObserver.class);
			// 由该对象调用具体方法
			method.invoke(mPm, packageName, mStatsObserver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearCache() {
		try {
			// 获取指定类的字节码文件
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			// 获取调用方法的对象
			Method method = clazz.getMethod("freeStorageAndNotify", long.class,
					IPackageDataObserver.class);
			// 由该对象调用具体方法
			method.invoke(mPm, Long.MAX_VALUE, new IPackageDataObserver.Stub() {
				@Override
				public void onRemoveCompleted(String packageName,
						boolean succeeded) throws RemoteException {
					Message msg = Message.obtain();
					msg.what = CLEAR_ALL_CACHE;
					mHandler.sendMessage(msg);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		final View view = View.inflate(this, R.layout.dialog_cache, null);
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText("恭喜您成功清除" + (int) totalCache / 1024 / 1024 + "MB缓存！");
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
