package com.lhp.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.activity.AppManagerActivity.MyAppAdapter;
import com.lhp.mobilesafe.db.bean.ProcessInfo;
import com.lhp.mobilesafe.engine.ProcessInfoProvider;

public class ProcessManagerActivity extends BaseActivity implements
		OnClickListener {

	private ProgressBar pb;
	private TextView tv_process_count, tv_memory_info;
	private ListView lv_process_list;
	private Button bt_select_all, bt_select_reverse, bt_clear;
	private int mProcessCount;
	private List<ProcessInfo> mProcessInfoList;

	private ArrayList<ProcessInfo> sysAppList;
	private ArrayList<ProcessInfo> myAppList;

	private ProcessInfo mProcessInfo;
	private long mAvailSpace;
	private String mStrTotalSpace;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
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

		};
	};
	private Button btn_myApp;
	private Button btn_sysApp;
	private MyAppAdapter myAppAdapter;
	private SysAppAdapter sysAppAdapter;
	private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);
		initView();
		initTitleData();
		initListData();
	}

	private void initListData() {
		getData();
	}

	private void getData() {
		new Thread() {
			public void run() {
				mProcessInfoList = ProcessInfoProvider
						.getProcessInfo(getApplicationContext());
				sysAppList = new ArrayList<ProcessInfo>();
				myAppList = new ArrayList<ProcessInfo>();
				for (ProcessInfo info : mProcessInfoList) {
					if (info.isSystem) {
						sysAppList.add(info);
					} else {
						if(!info.packageName.equals(getPackageName())){
							myAppList.add(info);
						}
					}
				}
				mHandler.sendEmptyMessage(1);
			};
		}.start();
	}

	private void initTitleData() {
		mProcessCount = ProcessInfoProvider.getProcessCount(this);
		tv_process_count.setText("进程总数:" + mProcessCount);
		// 获取可用内存大小并格式化数据
		mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
		String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);
		// 获取总内存大小并格式化数据
		long totalSpace = ProcessInfoProvider.getTotalSpace(this);
		mStrTotalSpace = Formatter.formatFileSize(this, totalSpace);
		tv_memory_info.setText("内存:" + strAvailSpace + "/" + mStrTotalSpace);
	}

	private void initView() {

		tv_title = (TextView) findViewById(R.id.tv_title);
		AssetManager mAssetManager = getAssets();
		Typeface tf = Typeface.createFromAsset(mAssetManager,
				"fonts/huawenxinkai.ttf");

		tv_title.setTypeface(tf);

		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);
		lv_process_list = (ListView) findViewById(R.id.lv_process_list);
		bt_select_all = (Button) findViewById(R.id.bt_select_all);
		bt_select_reverse = (Button) findViewById(R.id.bt_select_reverse);
		bt_clear = (Button) findViewById(R.id.bt_clear);
		btn_myApp = (Button) findViewById(R.id.btn_myApp);
		btn_sysApp = (Button) findViewById(R.id.btn_sysApp);
		pb = (ProgressBar) findViewById(R.id.pb);

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
	}

	private void setClickable() {
		btn_myApp.setOnClickListener(this);
		btn_sysApp.setOnClickListener(this);
		bt_clear.setOnClickListener(this);
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
		case R.id.bt_clear:
			killSelectedApp();
			break;
		}
	}

	private void initMyAppList() {
		
		btn_myApp.setBackgroundResource(R.drawable.btn_bg_blue);
		btn_myApp.setTextColor(Color.parseColor("#FFFFFF"));
		btn_sysApp.setBackgroundColor(Color.parseColor("#FFFFFF"));
		btn_sysApp.setTextColor(Color.parseColor("#000000"));
		
		myAppAdapter = new MyAppAdapter();
		lv_process_list.setAdapter(myAppAdapter);
		lv_process_list.setOnItemClickListener(new OnItemClickListener() {
			private CheckBox cb_box;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mProcessInfo = myAppList.get(position);
				mProcessInfo.isCheck = !mProcessInfo.isCheck;
				cb_box = (CheckBox) view.findViewById(R.id.cb_box);
				cb_box.setChecked(mProcessInfo.isCheck);
			}
		});
		bt_select_all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectAllMyApp();
			}
		});
		bt_select_reverse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectReverseMyApp();
			}
		});
	}

	private void initSysAppList() {
		btn_sysApp.setBackgroundResource(R.drawable.btn_bg_blue);
		btn_sysApp.setTextColor(Color.parseColor("#FFFFFF"));
		btn_myApp.setBackgroundColor(Color.parseColor("#FFFFFF"));
		btn_myApp.setTextColor(Color.parseColor("#000000"));
		sysAppAdapter = new SysAppAdapter();
		lv_process_list.setAdapter(sysAppAdapter);
		lv_process_list.setOnItemClickListener(new OnItemClickListener() {
			private CheckBox cb_box;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mProcessInfo = sysAppList.get(position);
				mProcessInfo.isCheck = !mProcessInfo.isCheck;
				cb_box = (CheckBox) view.findViewById(R.id.cb_box);
				cb_box.setChecked(mProcessInfo.isCheck);
			}
		});
		bt_select_all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectAllSysApp();
			}
		});
		bt_select_reverse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectReverseSysApp();
			}
		});
	}

	// 清理选中进程
	private void killSelectedApp() {
		List<ProcessInfo> killProcessList = new ArrayList<ProcessInfo>();
		for (ProcessInfo processInfo : myAppList) {
			if (processInfo.getPackageName().equals(getPackageName())) {
				continue;
			}
			if (processInfo.isCheck) {
				killProcessList.add(processInfo);
			}
		}

		for (ProcessInfo processInfo : sysAppList) {
			if (processInfo.isCheck) {
				killProcessList.add(processInfo);
			}
		}
		long totalReleaseSpace = 0;
		for (ProcessInfo processInfo : killProcessList) {
			// 6,判断当前进程在那个集合中,从所在集合中移除
			if (myAppList.contains(processInfo)) {
				myAppList.remove(processInfo);
			}

			if (sysAppList.contains(processInfo)) {
				sysAppList.remove(processInfo);
			}
			// 7,杀死记录在killProcessList中的进程
			ProcessInfoProvider.killProcess(this, processInfo);
			// 记录释放空间的总大小
			totalReleaseSpace += processInfo.memSize;
		}
		// 刷新ListView
		if (myAppAdapter != null) {
			myAppAdapter.notifyDataSetChanged();
		}
		if (sysAppAdapter != null) {
			sysAppAdapter.notifyDataSetChanged();
		}
		// 更新进程总数和剩余空间
		mProcessCount -= killProcessList.size();
		mAvailSpace += totalReleaseSpace;
		tv_process_count.setText("进程总数:" + mProcessCount);
		tv_memory_info.setText("剩余/总共"
				+ Formatter.formatFileSize(this, mAvailSpace) + "/"
				+ mStrTotalSpace);
		// 吐司提醒
		String totalRelease = Formatter.formatFileSize(this, totalReleaseSpace);
		Toast.makeText(
				getApplicationContext(),
				String.format("杀死了%d进程,释放了%s空间", killProcessList.size(),
						totalRelease), Toast.LENGTH_LONG).show();
	}

	private void selectReverseMyApp() {
		for (ProcessInfo processInfo : myAppList) {
			if (processInfo.getPackageName().equals(getPackageName())) {
				continue;
			}
			processInfo.isCheck = !processInfo.isCheck;
		}
		if (myAppAdapter != null) {
			myAppAdapter.notifyDataSetChanged();
		}
	}

	private void selectReverseSysApp() {
		for (ProcessInfo processInfo : sysAppList) {
			processInfo.isCheck = !processInfo.isCheck;
		}
		if (sysAppAdapter != null) {
			sysAppAdapter.notifyDataSetChanged();
		}
	}

	private void selectAllMyApp() {
		for (ProcessInfo processInfo : myAppList) {
			if (processInfo.getPackageName().equals(getPackageName())) {
				continue;
			}
			processInfo.isCheck = true;
		}
		if (myAppAdapter != null) {
			myAppAdapter.notifyDataSetChanged();
		}
	}

	private void selectAllSysApp() {
		for (ProcessInfo processInfo : sysAppList) {
			processInfo.isCheck = true;
		}
		if (sysAppAdapter != null) {
			sysAppAdapter.notifyDataSetChanged();
		}
	}

	class MyAppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return myAppList.size();
		}

		@Override
		public ProcessInfo getItem(int position) {
			return myAppList.get(position);
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
						R.layout.listview_process_item, null);
				holder = new MyAppViewHolder();
				holder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.tv_memory_info = (TextView) convertView
						.findViewById(R.id.tv_memory_info);
				holder.cb_box = (CheckBox) convertView
						.findViewById(R.id.cb_box);
				convertView.setTag(holder);
			} else {
				holder = (MyAppViewHolder) convertView.getTag();
			}
			holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
			holder.tv_name.setText(getItem(position).name);
			String strSize = Formatter.formatFileSize(getApplicationContext(),
					getItem(position).memSize);
			holder.tv_memory_info.setText("已使用内存:" + strSize);
			// 本进程不能被选中,所以先将checkbox隐藏掉
			if (getItem(position).packageName.equals(getPackageName())) {
				holder.cb_box.setVisibility(View.GONE);
			} else {
				holder.cb_box.setVisibility(View.VISIBLE);
			}
			holder.cb_box.setChecked(getItem(position).isCheck);
			return convertView;
		}
	}

	class SysAppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return sysAppList.size();
		}

		@Override
		public ProcessInfo getItem(int position) {
			return sysAppList.get(position);
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
						R.layout.listview_process_item, null);
				holder = new SysAppViewHolder();
				holder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.tv_memory_info = (TextView) convertView
						.findViewById(R.id.tv_memory_info);
				holder.cb_box = (CheckBox) convertView
						.findViewById(R.id.cb_box);
				convertView.setTag(holder);
			} else {
				holder = (SysAppViewHolder) convertView.getTag();
			}
			holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
			holder.tv_name.setText(getItem(position).name);
			String strSize = Formatter.formatFileSize(getApplicationContext(),
					getItem(position).memSize);
			holder.tv_memory_info.setText("已使用内存:" + strSize);
			// 本进程不能被选中,所以先将checkbox隐藏掉
			if (getItem(position).packageName.equals(getPackageName())) {
				holder.cb_box.setVisibility(View.GONE);
			} else {
				holder.cb_box.setVisibility(View.VISIBLE);
			}
			holder.cb_box.setChecked(getItem(position).isCheck);
			return convertView;
		}
	}

	static class MyAppViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_memory_info;
		CheckBox cb_box;
	}

	static class SysAppViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_memory_info;
		CheckBox cb_box;
	}
}
