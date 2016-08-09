package com.lhp.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberDBHelper extends SQLiteOpenHelper{

	/** 在构造方法中调用父类构造方法创建数据库 **/
	public BlackNumberDBHelper(Context context) {
		super(context, "blacknumber.db", null, 1); 
	}

	/** onCreate()中建表 **/
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table blacknumber " +
				"(_id integer primary key autoincrement , phone varchar(20), mode varchar(5));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

}
