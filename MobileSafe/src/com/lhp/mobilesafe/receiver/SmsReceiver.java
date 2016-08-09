package com.lhp.mobilesafe.receiver;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.service.LocationService;
import com.lhp.mobilesafe.utils.ConstantValue;
import com.lhp.mobilesafe.utils.SpUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// 判断是否开启了防盗保护
		boolean open_security = SpUtil.getBoolean(context,ConstantValue.OPEN_SECURITY, false);
		if(open_security){
			// 获取短信内容
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			// 循环遍历短信过程
			for (Object object : objects) {
				// 获取短信对象
				SmsMessage sms = SmsMessage.createFromPdu((byte[])object);
				// 获取短信对象的基本信息
				String originatingAddress = sms.getOriginatingAddress();
				String messageBody = sms.getMessageBody();
				// 判断是否包含播放报警音乐的关键字
				if(messageBody.contains("alarm")){
					// 播放音乐
					MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
					mediaPlayer.setLooping(true);
					mediaPlayer.start();
					// 震动
					Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(new long[]{2000, 5000}, 10); // (震动规则(不震时间,震动时间,不震时间,震动时间...), 重复次数)
				}
				
				if(messageBody.contains("location")){
					// 开启获取位置服务
					context.startService(new Intent(context,LocationService.class));
				}
				
				if(messageBody.contains("lockscrenn")){
				}
				
				if(messageBody.contains("wipedate")){
				}
			}
		}
	}
}
