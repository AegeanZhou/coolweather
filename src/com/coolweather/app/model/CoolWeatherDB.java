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
	private static CoolWeatherDB coolWeatherDB;
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
	public synchronized static CoolWeatherDB getInstance(Context context) {
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
			values.put("province_name", province.getProvinceName());
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
		if (city != null) {
			ContentValues value = new ContentValues();
			value.put("city_name", city.getCityName());
			value.put("city_code", city.getCityCode());
			value.put("province_id", city.getProvinceId());
			db.insert("city", null, value);
		}
	}

	/*
	 * 从数据库读取城市信息
	 */

	public List<City> loadCities(int provinceId) {
		List<City> citiesList = new ArrayList<City>();
		Cursor cursor = db.query("city", null, "province_id = ?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
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
		if (county != null) {
			ContentValues value = new ContentValues();
			value.put("county_name", county.getCountyName());
			value.put("county_Code", county.getCountyCode());
			value.put("city_id", county.getCityId());
			db.insert("county", null, value);
		}
	}

	/*
	 * 从数据库读取城镇信息
	 */
	public List<County> loadCounty(int cityId) {
		List<County> countiesList = new ArrayList<County>();
		Cursor cursor = db.query("county", null, "city_id = ?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToNext()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCityId(cityId);
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				countiesList.add(county);
			} while (cursor.moveToNext());
			if (cursor != null) {
				cursor.close();
			}
		}
		return countiesList;

	}
}
