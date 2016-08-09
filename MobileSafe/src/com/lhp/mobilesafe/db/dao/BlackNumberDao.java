package com.lhp.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lhp.mobilesafe.db.BlackNumberDBHelper;
import com.lhp.mobilesafe.db.bean.BlackNumberInfo;

/** 单例模式DAO类 **/
public class BlackNumberDao {

	private BlackNumberDBHelper mBlackNumberDBHelper;

	/** 私有化构造方法，并在其中创建数据库和表。 **/
	private BlackNumberDao(Context context) {
		mBlackNumberDBHelper = new BlackNumberDBHelper(context);
	}

	/** 创建当前类的静态对象 **/
	private static BlackNumberDao blackNumberDao = null;

	/** 提供静态的获取唯一实例的方法 **/
	public static BlackNumberDao getInstance(Context context) {
		if (blackNumberDao == null) {
			blackNumberDao = new BlackNumberDao(context);
		}
		return blackNumberDao;
	}

	/** 业务方法：插入数据 **/
	public void insert(String phone, String mode) {
		SQLiteDatabase db = mBlackNumberDBHelper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();
	}

	/** 业务方法：删除数据 **/
	public void delete(String phone) {
		SQLiteDatabase db = mBlackNumberDBHelper.getReadableDatabase();
		db.delete("blacknumber", "phone = ?", new String[] { phone });
		db.close();
	}

	/** 业务方法：更新数据 **/
	public void update(String phone, String mode) {
		SQLiteDatabase db = mBlackNumberDBHelper.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("mode", mode);
		db.update("blacknumber", contentValues, "phone = ?",
				new String[] { phone });
		db.close();
	}

	/** 业务方法：查询所有数据 **/
	public List<BlackNumberInfo> findAll() {
		SQLiteDatabase db = mBlackNumberDBHelper.getReadableDatabase();
		Cursor cursor = db.query("blacknumber",
				new String[] { "phone", "mode" }, null, null, null, null,
				"_id desc");
		List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.phone = cursor.getString(0);
			blackNumberInfo.mode = cursor.getString(1);
			blackNumberList.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return blackNumberList;
	}

	/** 业务方法：查询数据 **/
	public List<BlackNumberInfo> find(int index) {
		SQLiteDatabase db = mBlackNumberDBHelper.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select phone,mode from blacknumber order by _id desc limit ?,20;",
						new String[] { index + "" });
		List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.phone = cursor.getString(0);
			blackNumberInfo.mode = cursor.getString(1);
			blackNumberList.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return blackNumberList;
	}

	/** 业务方法：获取数据数目 **/
	public int getCount() {
		SQLiteDatabase db = mBlackNumberDBHelper.getReadableDatabase();
		int count = 0;
		Cursor cursor = db.rawQuery("select count(*) from blacknumber;", null);
		if (cursor.moveToNext()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}

	/** 业务方法：获取电话号码对应的拦截模式 **/
	public int getMode(String phone) {
		SQLiteDatabase db = mBlackNumberDBHelper.getReadableDatabase();
		int mode = 0;
		Cursor cursor = db.query("blacknumber", new String[] { "mode" },
				"phone = ?", new String[] { phone }, null, null, null);
		if (cursor.moveToNext()) {
			mode = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return mode;
	}

}
