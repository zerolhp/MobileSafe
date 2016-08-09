package com.lhp.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.lhp.mobilesafe.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class BlackNumberService extends Service {

	private InnerSmsReceiver mInnerSmsReceiver;
	private BlackNumberDao mDao;
	private TelephonyManager mTM;
	private MyPhoneStateListener mPhoneStateListener;

	@Override
	public void onCreate() {
		mDao = BlackNumberDao.getInstance(getApplicationContext());
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(Integer.MAX_VALUE); // 最高权限

		// 注册短信拦截接收器
		mInnerSmsReceiver = new InnerSmsReceiver();
		registerReceiver(mInnerSmsReceiver, intentFilter);

		// 电话管理者对象
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// 监听电话状态
		mPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/** 短信拦截接收器 **/
	class InnerSmsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(getApplicationContext(), "success",
					Toast.LENGTH_LONG).show();
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
				String originatingAddress = sms.getOriginatingAddress();
				String messageBody = sms.getMessageBody();
				int mode = mDao.getMode(originatingAddress);
				if (mode == 1 || mode == 3) {
					abortBroadcast(); // 拦截短信
				}
			}
		}
	}

	/** 拨号状态监听器 **/
	class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: // 空闲
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // 摘机
				break;
			case TelephonyManager.CALL_STATE_RINGING: // 响铃
				endCall(incomingNumber);
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	/** 挂断电话 **/
	public void endCall(String phone) {
		int mode = mDao.getMode(phone);
		if (mode == 2 || mode == 3) {
			try {
				// 1、获取ServiceManager的字节码文件
				Class<?> clazz = Class.forName("android.os.ServiceManager");
				// 2、获取指定的方法
				Method method = clazz.getMethod("getService", String.class);
				// 3、反射调用此方法，并定义为IBinder对象。
				IBinder iBinder = (IBinder) method.invoke(null,
						Context.TELEPHONY_SERVICE);
				// 4、接入远程方法
				ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
				// 5、使用方法
				iTelephony.endCall();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** onDestory()中取消注册广播 **/
	@Override
	public void onDestroy() {
		if (mInnerSmsReceiver != null) {
			unregisterReceiver(mInnerSmsReceiver);
		}
		super.onDestroy();
	}

}
