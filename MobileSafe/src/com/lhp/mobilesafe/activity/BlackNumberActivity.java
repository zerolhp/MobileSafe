package com.lhp.mobilesafe.activity;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.db.bean.BlackNumberInfo;
import com.lhp.mobilesafe.db.dao.BlackNumberDao;

public class BlackNumberActivity extends BaseActivity {

	private Button bt_add;
	private ListView lv_blacknumber;
	private List<BlackNumberInfo> mBlackNumberList;
	private boolean mIsLoad = false; // 放置重复加载
	private int mCount;
	private MyAdapter mAdapter;
	private BlackNumberDao mDao;
	private int mode = 1;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 4,告知listView可以去设置数据适配器
			if (mAdapter == null) {
				mAdapter = new MyAdapter();
				lv_blacknumber.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);

		initView();
		initData();
	}

	private void initView() {
		AssetManager mAssetManager = getAssets();
		Typeface tf = Typeface.createFromAsset(mAssetManager, "fonts/huawenxinkai.ttf");
		TextView tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setTypeface(tf);
		bt_add = (Button) findViewById(R.id.bt_add);
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
		

		bt_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});

		// 监听其滚动状态
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (mBlackNumberList != null) {
					if (scrollState == OnScrollListener.SCROLL_STATE_IDLE // 空闲状态
							&& lv_blacknumber.getLastVisiblePosition() >= mBlackNumberList
									.size() - 1 // 滑动到底部
							&& !mIsLoad) { // 正在加载
						if (mCount > mBlackNumberList.size()) { // 加载下一页
							new Thread() {
								public void run() {
									mDao = BlackNumberDao
											.getInstance(getApplicationContext());
									List<BlackNumberInfo> moreData = mDao
											.find(mBlackNumberList.size());
									mBlackNumberList.addAll(moreData);
									mHandler.sendEmptyMessage(0);
								}
							}.start();
						}
					}
				}

			}

			// 滚动回调
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	private void initData() {
		new Thread() {
			public void run() {
				mDao = BlackNumberDao.getInstance(getApplicationContext());
				mBlackNumberList = mDao.find(0);
				mCount = mDao.getCount();
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	protected void showDialog() {
		Builder builder = new AlertDialog.Builder(this);

		final AlertDialog dialog = builder.create();
		View view = View.inflate(getApplicationContext(),
				R.layout.dialog_add_blacknumber, null);
		dialog.setView(view, 0, 0, 0, 0);

		final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);

		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		// 监听其选中条目的切换过程
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_sms:
					// 拦截短信
					mode = 1;
					break;
				case R.id.rb_phone:
					// 拦截电话
					mode = 2;
					break;
				case R.id.rb_all:
					// 拦截所有
					mode = 3;
					break;
				}
			}
		});

		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString().trim();
				if (!TextUtils.isEmpty(phone)) {
					mDao.insert(phone, mode + "");
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
					blackNumberInfo.phone = phone;
					blackNumberInfo.mode = mode + "";
					mBlackNumberList.add(0, blackNumberInfo);
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
					dialog.dismiss();
				} else {
					Toast.makeText(getApplicationContext(), "请输入拦截号码",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mBlackNumberList.size();
		}

		@Override
		public Object getItem(int position) {
			return mBlackNumberList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.listview_blacknumber_item, null);
				holder = new ViewHolder();
				holder.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_phone);
				holder.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mDao.delete(mBlackNumberList.get(position).phone);
					mBlackNumberList.remove(position);
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
				}
			});

			holder.tv_phone.setText(mBlackNumberList.get(position).phone);
			int mode = Integer.parseInt(mBlackNumberList.get(position).mode);
			switch (mode) {
			case 1:
				holder.tv_mode.setText("拦截短信");
				break;
			case 2:
				holder.tv_mode.setText("拦截电话");
				break;
			case 3:
				holder.tv_mode.setText("拦截所有");
				break;
			}
			holder.tv_phone.setTextSize(18);
			holder.tv_mode.setTextSize(13);
			return convertView;
		}
	}

	/** 静态控件持有类 **/
	static class ViewHolder {
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}

}
