package com.coolweather.app.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.db.CoolWeatherOpenHelper;

public class CoolWeatherDB {
	private String DBName = "CoolWeatherDB";
	public static final int VERSION = 1;
	private CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	/*
	 * 创建数据库构造函数，并将其私有化
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper helper = new CoolWeatherOpenHelper(context,
				DBName, null, VERSION);
		db = helper.getWritableDatabase();
	}

	/*
	 * 获取CoolWeather实例
	 */
	public CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/*
	 * 将Province实例信息保存到数据库
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceCode());
			values.put("province_code", province.getProvinceCode());
			db.insert("province", null, values);
		}
	}

	/*
	 * 从数据库读取省份信息
	 */
	public List<Province> loadProvinces() {
		List<Province> provincesList = new ArrayList<Province>();

		Cursor cursor = db
				.query("province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				provincesList.add(province);
			} while (cursor.moveToNext());
			if (cursor != null) {
				cursor.close();
			}
		}
		return provincesList;

	}

	/*
	 * 保存city实例信息到数据库
	 */
	public void saveCity(City city) {
		ContentValues value = new ContentValues();
		value.put("id", city.getId());
		value.put("city_name", city.getCityName());
		value.put("city_code", city.getCityCode());
		value.put("province_id", city.getProvinceId());
		db.insert("city", null, value);
	}

	/*
	 * 从数据库读取城市信息
	 */

	public List<City> loadCities() {
		City city = new City();
		List<City> citiesList = new ArrayList<City>();
		Cursor cursor = db.query("city", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor
						.getColumnIndex("province_id")));
				citiesList.add(city);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return citiesList;
	}

	/*
	 * 保存county信息到数据库
	 */

	public void saveCounty(County county) {
		ContentValues value = new ContentValues();
		value.put("id", county.getId());
		value.put("county_name", county.getCountyName());
		value.put("county_Code", county.getCountyCode());
		value.put("city_id", county.getCityId());
		db.insert("county", null, value);
	}

	/*
	 * 从数据库读取城镇信息
	 */
	public List<County> loadCounty() {
		County county = new County();
		List<County> countiesList = new ArrayList<County>();
		Cursor cursor = db.query("city", null, null, null, null, null, null);
		if (cursor.moveToNext()) {
			do {
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("city_name")));
			} while (cursor.moveToNext());
			if (cursor != null) {
				cursor.close();
			}
		}
		return countiesList;

	}
}
