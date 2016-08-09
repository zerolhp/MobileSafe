package com.lhp.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.youmi.android.AdManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.utils.ConstantValue;
import com.lhp.mobilesafe.utils.SpUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class SplashActivity extends BaseActivity {

	/** 状态码 **/
	protected static final int UPDATE_VERSION = 100;
	protected static final int ENTER_HOME = 101;
	protected static final int URL_ERROR = 102;
	protected static final int IO_ERROR = 103;
	protected static final int JSON_ERROR = 104;
	protected static final String TAG = "SplashActivity";

	private int mLocalVersionCode;
	private RelativeLayout rl_root;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			enterHome();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		AdManager.getInstance(this).init("9d558866185500e2", "9e501003a73f1ce8", true);
		//AdManager.getInstance(this).init("2f2c31cc7b078bd0", "bbb521418b4757dd", true);

		initView();
		initData();
		initAnimation();
		initDB("address.db");
		initDB("commonnum.db");
		initDB("antivirus.db");
	}

	private void initView() {
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}

	private void initData() {
		// 获取本地版本号
		mLocalVersionCode = getVersionCode();
		Log.i(TAG, "LocalVersion:" + mLocalVersionCode);
		if (SpUtil.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_UPDATE, true)) {
			// 获取服务器版本号
			checkVersion();
		} else {
			mHandler.sendEmptyMessageDelayed(0, 3000);
		}

	}

//	private String getVersionName() {
//		PackageManager pm = getPackageManager();
//		try {
//			PackageInfo info = pm.getPackageInfo(getPackageName(), 0); // 0代表获取基本信息
//			return info.versionName;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	private int getVersionCode() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0); // 0代表获取基本信息
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/** 检查版本 **/
	private void checkVersion() {
		HttpUtils mHtpHttpUtils = new HttpUtils(500); // 请求超时时间为5S
		mHtpHttpUtils.send(HttpMethod.GET,
				"http://192.168.62.33:8080/MobileSafe/updateInfo.json",
				new RequestCallBack<String>() {
					private String mServerVersionCode;

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						try {
							JSONObject mJSONObject = new JSONObject(
									response.result);
							mServerVersionCode = mJSONObject
									.getString("versionCode");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						Log.i(TAG, "ServerVersion:" + mServerVersionCode);
						mHandler.sendEmptyMessageDelayed(0, 2500); // 延迟5S发送空消息
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						mHandler.sendEmptyMessageDelayed(0, 2500);
					}
				});
	}

	protected void enterHome() {
		startActivity(new Intent(this, HomeActivity.class));
		finish();
	}

	/** 淡入动画 **/
	private void initAnimation() {
		Animation animation = new AlphaAnimation(0, 1);
		animation.setDuration(2500);
		animation.setFillAfter(true);
		rl_root.startAnimation(animation);
	}
	

	private void initDB(String dbName) {
		//1,在files文件夹下创建同名dbName数据库文件过程
		File files = getFilesDir();
		File file = new File(files, dbName);
		if(file.exists()){
			return;
		}
		InputStream stream = null;
		FileOutputStream fos = null;
		//2,输入流读取第三方资产目录下的文件
		try {
			stream = getAssets().open(dbName);
			//3,将读取的内容写入到指定文件夹的文件中去
			fos = new FileOutputStream(file);
			//4,每次的读取内容大小
			byte[] bs = new byte[1024];
			int temp = -1;
			while( (temp = stream.read(bs))!=-1){
				fos.write(bs, 0, temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(stream!=null && fos!=null){
				try {
					stream.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed(); //注释掉这行,back键不退出activity
	}

}
