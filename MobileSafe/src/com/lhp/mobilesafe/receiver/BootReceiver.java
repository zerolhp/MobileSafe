package com.lhp.mobilesafe.receiver;

import com.lhp.mobilesafe.utils.ConstantValue;
import com.lhp.mobilesafe.utils.SpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/** 待手机启动后查询SIM卡是否被更换 **/
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 获取开机后SIM卡的序列号
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerialNumber = tm.getSimSerialNumber()+"xxx";
		// 存储的序列号
		String sim_number = SpUtil.getString(context,ConstantValue.SIM_NUMBER, "");
		// 比对是否一致
		if(!simSerialNumber.equals(sim_number)){
			// 发送短信给紧急联系人，告知SIM卡被变更。
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(SpUtil.getString(context, ConstantValue.CONTACT_PHONE, "18610020187"), 
					null, "sim change!!!", null, null);
		}
	}
}
