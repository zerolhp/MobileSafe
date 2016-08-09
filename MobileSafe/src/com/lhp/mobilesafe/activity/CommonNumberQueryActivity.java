package com.lhp.mobilesafe.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.lhp.mobilesafe.R;
import com.lhp.mobilesafe.db.dao.CommonnumDao;
import com.lhp.mobilesafe.db.dao.CommonnumDao.Child;
import com.lhp.mobilesafe.db.dao.CommonnumDao.Group;

public class CommonNumberQueryActivity extends BaseActivity {
	private ExpandableListView elv_common_number;
	private List<com.lhp.mobilesafe.db.dao.CommonnumDao.Group> mGroup;
	private MyAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
		initView();
		initData();
	}

	private void initData() {
		CommonnumDao commonnumDao = new CommonnumDao();
		mGroup = commonnumDao.getGroup();

		mAdapter = new MyAdapter();
		elv_common_number.setAdapter(mAdapter);
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				startCall(mAdapter.getChild(groupPosition, childPosition).number);
				return false;
			}
		});
	}

	protected void startCall(String number) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + number));
		startActivity(intent);
	}

	private void initView() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
	}

	class MyAdapter extends BaseExpandableListAdapter {
		
		@Override
		public Child getChild(int groupPosition, int childPosition) {
			return mGroup.get(groupPosition).childList.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.elv_child_item, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_number = (TextView) view.findViewById(R.id.tv_number);

			tv_name.setText(getChild(groupPosition, childPosition).name);
			tv_number.setText(getChild(groupPosition, childPosition).number);

			return view;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mGroup.get(groupPosition).childList.size();
		}

		@Override
		public Group getGroup(int groupPosition) {
			return (Group) mGroup.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return mGroup.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setText("			" + getGroup(groupPosition).name);
			textView.setTextColor(Color.parseColor("#3d85c6"));
			textView.setTextSize(20);
			return textView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		// 孩子节点是否响应事件
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
}
