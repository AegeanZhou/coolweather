package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	/*
	 * province�������
	 */
	public static final String CREATE_PROVINCE = "create table province("
			+ "id integer primary key autoincrement," + "province_name text,"
			+ "province_code text)";
	/*
	 * city�������
	 */

	public static final String CREATE_CITY = "create table city("
			+ "id integer primary key autoincrement," + "city_name text,"
			+ "city_code text," + "province_id integer)";
	/*
	 * county�������
	 */

	public static final String CREATE_COUNTY = "create table county("
			+ "id integer primary key autoincrement," + "county_name text,"
			+ "county_code text," + "city_id integer)";

	public static final String CREATE_SELECTED = "create table selected("
			+ "id integer primary key autoincrement,"
			+ "county_name text unique," + "county_code text)";

	public static final String CREATE_AREA = "create table area("
			+ "id integer primary key autoincrement," + "citynm text,"
			+ "weaid text)";

	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);// ����province��
		db.execSQL(CREATE_CITY);// ����city��
		db.execSQL(CREATE_COUNTY);// ����county��
		db.execSQL(CREATE_SELECTED);// ����selected��
		db.execSQL(CREATE_AREA);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
		case 1:
			db.execSQL(CREATE_SELECTED);
		case 2:
			db.execSQL(CREATE_AREA);
		case 3:
			db.execSQL("drop table if exists area");
			db.execSQL(CREATE_AREA);
		case 4:
			db.execSQL("drop table if exists selected");
			db.execSQL(CREATE_SELECTED);

		default:
		}

	}

}
