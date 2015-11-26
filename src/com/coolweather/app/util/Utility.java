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

	// 解析返回的Jason数据
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
				String weaid = obj.getString("weaid");

				for (int i = 0; i < array.length(); i++) {
					obj = array.getJSONObject(i);
					String temperature = obj.getString("temperature");
					String days = obj.getString("days");
					String weather_icon = obj.getString("weather_icon");
					String high_temp = obj.getString("temp_high");
					String low_temp = obj.getString("temp_low");
					String date = high_temp + "#" + low_temp + "#"
							+ weather_icon + "#" + days + "#" + temperature;
					// Log.i("utility", date);
					list.add(date);
				}

				saveWeatherInf(context, weaid, tempNow, weather, wind, winp,
						list);
			} else {
				String msg = json.getString("msg");
				String error = "更新失败...";
				// Log.i("handleWeatherResponseT", error + msg);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 将解析出来的天气信息保存到sharedpreference
	private static void saveWeatherInf(Context context, String weaid,
			String tempNow, String weather, String wind, String winp,
			List<String> list) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putString("weaid", weaid);
		editor.putString("tempNow", tempNow);
		editor.putString("weather", weather);
		editor.putString("wind", wind);
		editor.putString("winp", winp);
		String[] week = { "0", "1", "2", "3", "4", "5", "6" };
		for (int i = 0; i < list.size(); i++) {
			String key = week[i];
			editor.putString(key, list.get(i));
		}

		editor.commit();

	}

	// 解析各个地区的citynm对应的weaid并保存到数据库
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