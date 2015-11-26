package com.coolweather.app.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.coolweather.app.db.CoolWeatherOpenHelper;

public class CoolWeatherDB {
	private String DBName = "CoolWeatherDB";
	public static final int VERSION = 5;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	/*
	 * �������ݿ⹹�캯����������˽�л�
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper helper = new CoolWeatherOpenHelper(context,
				DBName, null, VERSION);
		db = helper.getWritableDatabase();
	}

	/*
	 * ��ȡCoolWeatherʵ��
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/*
	 * ��Provinceʵ����Ϣ���浽���ݿ�
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
	 * �����ݿ��ȡʡ����Ϣ
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
	 * ����cityʵ����Ϣ�����ݿ�
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
	 * ����county_code��ѯcountyName
	 */
	public County queryCounty(String county_name) {
		County county = new County();
		if (county_name != null) {
			Log.i("coolweather", county_name);
			Cursor cur = db.query("county", null, "county_name = ?",
					new String[] { county_name }, null, null, null);
			if (cur.moveToFirst()) {
				do {
					county.setCountyName(county_name);
					county.setCountyCode(cur.getString(cur
							.getColumnIndex("county_code")));

					Log.i("coolweather",
							county.getCountyCode() + county.getCountyName());

				} while (cur.moveToNext());
				if (cur != null) {
					cur.close();
				}
			}
		}
		Log.i("coolweather", county.getCountyCode() + county.getCountyName());
		return county;
	}

	/*
	 * �����ݿ��ȡ������Ϣ
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
	 * ����county��Ϣ�����ݿ�
	 */

	public void saveCounty(County county) {
		if (county != null) {
			ContentValues value = new ContentValues();
			value.put("county_name", county.getCountyName());
			value.put("county_code", county.getCountyCode());
			value.put("city_id", county.getCityId());
			db.insert("county", null, value);
		}
	}

	/*
	 * �����ݿ��ȡ������Ϣ
	 */
	public List<County> loadCounty(int cityId) {
		List<County> countiesList = new ArrayList<County>();
		Cursor cursor = db.query("county", null, "city_id = ?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst()) {
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

	/*
	 * 
	 * County county = new County(); if (county_name != null) { Cursor cur =
	 * db.query("county", null, "county_name = ?", new String[] { county_name },
	 * null, null, null); if (cur.moveToFirst()) { do {
	 */

	public void saveSelectedCounty(County county) {

		if (county != null) {
			ContentValues value = new ContentValues();
			value.put("county_name", county.getCountyName());
			value.put("county_code", county.getCountyCode());

			db.replace("selected", null, value);

		}
	}

	public void delSelectedCounty(String name) {
		if (name != null) {
			db.delete("selected", "county_name=?", new String[] { name });
		}
	}

	public void delSelectedCounty(int id) {
		if (id > 0) {
			db.delete("selected", "id=?", new String[] { "id" });
		}
	}

	public List<County> loadSelected() {
		List<County> selectedList = new ArrayList<County>();
		Cursor cursor = db
				.query("selected", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				// county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				selectedList.add(county);
			} while (cursor.moveToNext());
			if (cursor != null) {
				cursor.close();
			}
		}
		return selectedList;
	}

	public void saveArea(Area area) {
		if (area != null) {
			ContentValues value = new ContentValues();
			value.put("citynm", area.getCitynm());
			value.put("weaid", area.getWeaid());
			db.insert("area", null, value);
		}
	}

	public List<Area> loadArea() {
		List<Area> areaList = new ArrayList<Area>();
		Cursor cursor = db.query("area", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Area area = new Area();
				area.setId(cursor.getInt(cursor.getColumnIndex("id")));
				area.setCitynm(cursor.getString(cursor.getColumnIndex("citynm")));
				area.setWeaid(cursor.getString(cursor.getColumnIndex("weaid")));
				areaList.add(area);
			} while (cursor.moveToNext());
			if (cursor != null) {
				cursor.close();
			}
		}
		// db.delete("area", null, null);
		return areaList;
	}
}
