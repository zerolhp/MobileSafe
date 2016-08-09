package com.lhp.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lhp.mobilesafe.R;

/** 组合型自定义控件 **/
public class SettingItemView extends RelativeLayout{

	public TextView tv_title;
	public CheckBox cb_box;
	public TextView tv_des;
	public String mDestitle;
	public String mDesoff;
	public String mDeson;
	public View rootView;

	public SettingItemView(Context context) {
		this(context,null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	/** 最底层构造方法 **/
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		View.inflate(context, R.layout.setting_item_view, this);
		rootView =  findViewById(R.id.rl_root);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		cb_box = (CheckBox) findViewById(R.id.cb_box);
	}

	public void setCheck(boolean isCheck){
		cb_box.setChecked(isCheck);
		if(isCheck){
			tv_des.setText(mDeson);
		}else{
			tv_des.setText(mDesoff);
		}
	}
	
}
