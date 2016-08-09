package com.lhp.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.lhp.mobilesafe.R;

public class ContactListActivity extends BaseActivity {

	private ListView lv_contact;
	private List<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
	private MyAdapter mAdapter;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapter();
			lv_contact.setAdapter(mAdapter);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		initView();
		initData();
	}

	private void initView() {
		lv_contact = (ListView) findViewById(R.id.lv_contact);
		lv_contact.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mAdapter != null) {
					HashMap<String, String> hashMap = mAdapter
							.getItem(position);
					String number = hashMap.get("number");
					Intent intent = new Intent();
					intent.putExtra("number", number);
					setResult(0, intent); // 返回点击的电话号码
					finish();
				}
			}
		});
	}

	private void initData() {
		// 因为读取系统联系人,可能是一个耗时操作,放置到子线程中处理
		new Thread() {
			public void run() {
				// 查询系统联系人数据库表过程(读取联系人权限)
				Cursor cursor = getContentResolver().query(Phone.CONTENT_URI,
						null, null, null, null);
				// 循环游标
				while (cursor.moveToNext()) {
					HashMap<String, String> hashMap = new HashMap<String, String>();
					String name = cursor.getString(cursor
							.getColumnIndex(Phone.DISPLAY_NAME));
					String number = cursor.getString(cursor
							.getColumnIndex(Phone.NUMBER));
					if (!TextUtils.isEmpty(name)) {
						hashMap.put("name", name);
						Log.i("ContactList", name);
						hashMap.put("number", number);
						Log.i("ContactList", number);
					}
					contactList.add(hashMap);
				}
				cursor.close();
				mHandler.sendEmptyMessage(0);
			};
		}.start();

	}

	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return contactList.size();
		}
		@Override
		public HashMap<String, String> getItem(int position) {
			return contactList.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.listview_contact_item, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(LinearLayout.
                    LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			tv_name.setLayoutParams(params);
			tv_phone.setLayoutParams(params);
			tv_name.setTextSize(18);
			tv_phone.setTextSize(15);
			tv_name.setText(getItem(position).get("name"));
			tv_phone.setText(getItem(position).get("number"));
			return view;
		}
	}

}
