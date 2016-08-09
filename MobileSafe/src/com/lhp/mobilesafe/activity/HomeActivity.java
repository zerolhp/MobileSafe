package com.lhp.mobilesafe.activity;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.service.BlackNumberService;
import com.lhp.mobilesafe.service.SuspendingViewService;
import com.lhp.mobilesafe.utils.ConstantValue;
import com.lhp.mobilesafe.utils.Md5Util;
import com.lhp.mobilesafe.utils.SpUtil;

public class HomeActivity extends BaseActivity {

	private GridView gv_home;
	private String[] mTitleStrs;
	private int[] mDrawableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		// 实例化广告条
		AdView adView = new AdView(getApplicationContext(), AdSize.FIT_SCREEN);
		// 获取要嵌入广告条的布局
		LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);
		// 将广告条加入到布局中
		adLayout.addView(adView);
		
		initView();
		initData();
		initService();
	}

	private void initView() {
		gv_home = (GridView) findViewById(R.id.gv_home);
		ImageButton btn_clearCache = (ImageButton) findViewById(R.id.btn_clearCache);
		ScaleAnimation anim = new ScaleAnimation((float) 0.7, 1, (float) 0.7,
				1, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(3200);
		anim.setRepeatCount(-1);
		btn_clearCache.startAnimation(anim);

		btn_clearCache.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						CacheClearActivity.class));
			}
		});
	}

	private void initData() {
		mTitleStrs = new String[] { "手机防盗", "黑名单", "软件管理", "进程管理", "手机杀毒",
				"缓存清理", "高级工具", "设置中心" };
		mDrawableIds = new int[] { R.drawable.home_fangdao_3,
				R.drawable.home_heimingdan_3, R.drawable.home_ruanjian_3,
				R.drawable.home_xiancheng_3, R.drawable.home_shadu_3,
				R.drawable.home_huancun_3, R.drawable.home_gongju_3,
				R.drawable.home_setting_3 };
		gv_home.setAdapter(new MyAdapter());
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0: // 手机防盗
					showDialog();
					break;
				case 1: // 黑名单
					startActivity(new Intent(getApplicationContext(),
							BlackNumberActivity.class));
					break;
				case 2: // 软件管理
					startActivity(new Intent(getApplicationContext(),
							AppManagerActivity.class));
					break;
				case 3: // 进程管理
					startActivity(new Intent(getApplicationContext(),
							ProcessManagerActivity.class));
					break;
				case 4: // 手机杀毒
					startActivity(new Intent(getApplicationContext(),
							AnitVirusActivity.class));
					break;
				case 5: // 缓存清理
					startActivity(new Intent(getApplicationContext(),
							CacheClearActivity.class));
					break;
				case 6: // 高级工具
					startActivity(new Intent(getApplicationContext(),
							AToolActivity.class));
					break;
				case 7: // 设置中心
					Intent intent = new Intent(getApplicationContext(),
							SettingActivity.class);
					startActivity(intent);
					break;
				}
			}
		});
	}

	private void showDialog() {
		String psd = SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
		if (TextUtils.isEmpty(psd)) {
			showSetPsdDialog();
		} else {
			showConfirmPsdDialog();
		}
	}

	private void showSetPsdDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		final View view = View.inflate(this, R.layout.dialog_set_psd, null);
		dialog.setView(view); // 自定义对话框布局
		dialog.show();

		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et_set_psd = (EditText) view
						.findViewById(R.id.et_set_psd);
				EditText et_confirm_psd = (EditText) view
						.findViewById(R.id.et_confirm_psd);

				String psd = et_set_psd.getText().toString();
				String confirmPsd = et_confirm_psd.getText().toString();

				if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)) {
					if (psd.equals(confirmPsd)) {
						Toast.makeText(getApplicationContext(), "登录成功",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						Intent intent = new Intent(getApplicationContext(),
								Setup1Activity.class);
						startActivity(intent);
						finish();
						SpUtil.putString(getApplicationContext(),
								ConstantValue.MOBILE_SAFE_PSD,
								Md5Util.encoder(confirmPsd));
					} else {
						Toast.makeText(getApplicationContext(), "确认密码错误",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "请输入密码",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

	}

	private void showConfirmPsdDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		final View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
		dialog.setView(view);
		dialog.show();

		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et_confirm_psd = (EditText) view
						.findViewById(R.id.et_confirm_psd);
				String confirmPsd = et_confirm_psd.getText().toString();
				if (!TextUtils.isEmpty(confirmPsd)) {
					String psd = SpUtil.getString(getApplicationContext(),
							ConstantValue.MOBILE_SAFE_PSD, "");
					if (psd.equals(Md5Util.encoder(confirmPsd))) {
						Toast.makeText(getApplicationContext(), "登录成功",
								Toast.LENGTH_SHORT).show();
						startActivity(new Intent(getApplicationContext(),
								Setup1Activity.class));
						dialog.dismiss();
					} else {
						Toast.makeText(getApplicationContext(), "密码错误",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "请输入密码",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	private void initService() {
		startService(new Intent(getApplicationContext(),
				SuspendingViewService.class));
		startService(new Intent(getApplicationContext(),
				BlackNumberService.class));
	}

	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mTitleStrs.length;
		}

		@Override
		public Object getItem(int position) {
			return mTitleStrs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.gridview_item, null);
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);

			tv_title.setText(mTitleStrs[position]);
			iv_icon.setImageResource(mDrawableIds[position]);

			return view;
		}
	}

}
