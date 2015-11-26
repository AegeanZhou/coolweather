package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.model.Area;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class Utility {
	/*
	 * 解析从服务器得到的省份信息
	 */
	public synchronized static boolean handleProvinceResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String pro : allProvinces) {
					String[] array = pro.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/*
	 * 解析从服务器得到的city信息
	 */
	public static boolean handleCityResponse(String response,
			CoolWeatherDB coolWeatherDB, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String cit : allCities) {
					String[] array = cit.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;

	}/*
	 * 解析从服务器得到的county信息
	 */

	public static boolean handleCountyResponse(String response,
			CoolWeatherDB coolWeatherDB, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounty = response.split(",");
			if (allCounty != null && allCounty.length > 0) {
				for (String cou : allCounty) {
					String[] array = cou.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;

	}

	public static void handleWeatherResponseTwo(Context context, String response) {
		try {
			JSONObject object = new JSONObject(response);
			JSONObject info = object.getJSONObject("weatherinfo");
			String tempNow = info.getString("temp");
			String wind = info.getString("WD");
			String windLevel = info.getString("WS");
			saveNewWeatherInfo(context, tempNow, wind, windLevel);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void saveNewWeatherInfo(Context context, String tempNow,
			String wind, String windLevel) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putString("temp_now", tempNow);
		editor.putString("wind", wind);
		editor.putString("wind_level", windLevel);
		editor.commit();

	}

	public static void handleWeatherResponse(Context context, String response) {
		try {
			JSONObject json = new JSONObject(response);
			JSONObject weatherinfo = json.getJSONObject("weatherinfo");
			String cityName = weatherinfo.getString("city");
			String weatherCode = weatherinfo.getString("cityid");
			String temp1 = weatherinfo.getString("temp1");
			String temp2 = weatherinfo.getString("temp2");
			String publishTime = weatherinfo.getString("ptime");
			String weatherDesp = weatherinfo.getString("weather");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
					publishTime, weatherDesp);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String publishTime,
			String weatherDesp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年m月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("publish_time", publishTime);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_code", weatherCode);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("current_time", sdf.format(new Date()));

		editor.commit();

	}

	public static void handleWeatherResponseT(Context context, String response) {
		List<String> list = new ArrayList<String>();

		try {
			JSONObject json = new JSONObject(response);
			String result = json.getString("success");

			Log.i("handleWeatherResponseT", result);

			if ("1".equals(result)) {
				JSONArray array = json.getJSONArray("result");
				JSONObject obj = array.getJSONObject(0);
				String tempNow = obj.getString("temp_high");
				String weather = obj.getString("weather");
				String wind = obj.getString("wind");
				String winp = obj.getString("winp");

				for (int i = 0; i < array.length(); i++) {
					obj = array.getJSONObject(i);
					String temperature = obj.getString("temperature");
					String days = obj.getString("days");
					String weather_icon = obj.getString("weather_icon");
					String high_temp = obj.getString("temp_high");
					String low_temp = obj.getString("temp_low");
					String date = high_temp + "#" + low_temp + "#"
							+ weather_icon + "#" + days + "#"
							+ temperature;
//					Log.i("utility", date);
					list.add(date);
				}

				saveWeatherInf(context, tempNow, weather, wind, winp, list);
			} else {
				String msg = json.getString("msg");
				String error = "更新失败...";
//				Log.i("handleWeatherResponseT", error + msg);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void saveWeatherInf(Context context, String tempNow,
			String weather, String wind, String winp, List<String> list) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putString("tempNow", tempNow);
		editor.putString("weather", weather);
		editor.putString("wind", wind);
		editor.putString("winp", winp);
		String[] week = { "0", "1", "2", "3", "4", "5", "6" };
		for (int i = 0; i < list.size(); i++) {
			String key = week[i];
			editor.putString(key, list.get(i));
		}

//		Log.i("handleWeatherResponseT", "tempNow" + tempNow + "weather"
//				+ weather);
		for (int i = 0; i < list.size(); i++) {
			Log.i("handleWeatherResponseT", "wind" + wind + "winp" + winp + "*"
					+ list.get(i));
		}

		editor.commit();

	}

	public static boolean handleAreaRequest(CoolWeatherDB coolWeatherDB,
			String response) {
		try {
			JSONObject json = new JSONObject(response);
			String str = json.getString("success");
			if ("1".equals(str)) {
				JSONObject result = json.getJSONObject("result");
				for (int i = 1; i < 2646; i++) {
					if (result.has(i + "")) {
						JSONObject city = result.getJSONObject(i + "");
						Area area = new Area();
						area.setCitynm(city.getString("citynm"));
						area.setWeaid(city.getString("weaid"));
						coolWeatherDB.saveArea(area);
						// Log.i("Utility", i + "--" + area.getCitynm() + "--"
						// + area.getWeaid());
					}

				}
				Log.i("length", result.length() + "");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}