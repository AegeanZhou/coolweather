package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {
	private TextView title;
	private TextView publish;
	// private TextView time;
	private TextView temp1;
	private TextView temp2;
	private TextView weather;
	private LinearLayout weatherInfoLayout;
	private Button change;
	private Button refresh;
	private String countyCode;
	private ImageView image;
	private int weather_icon = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		title = (TextView) findViewById(R.id.title);
		publish = (TextView) findViewById(R.id.publish);
		// time = (TextView) findViewById(R.id.time);
		temp1 = (TextView) findViewById(R.id.temp1);
		temp2 = (TextView) findViewById(R.id.temp2);
		weather = (TextView) findViewById(R.id.weather);

		image = (ImageView) findViewById(R.id.weather_icon);

		change = (Button) findViewById(R.id.change);
		refresh = (Button) findViewById(R.id.refresh);
		change.setOnClickListener(this);
		refresh.setOnClickListener(this);

		countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			publish.setText("����ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			title.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			showWeather();
		}
	}

	private int getWeatherIcon(String weatherInfo) {
		if (weatherInfo.equals("��")) {
			return R.drawable.weathericon_condition_01;
		} else if (weatherInfo.equals("����")) {
			return R.drawable.weathericon_condition_02;
		} else if (weatherInfo.equals("��")) {
			return R.drawable.weathericon_condition_04;
		} else if (weatherInfo.equals("��")) {
			return R.drawable.weathericon_condition_05;
		} else if (weatherInfo.equals("ɳ����")) {
			return R.drawable.weathericon_condition_06;
		} else if (weatherInfo.equals("����")) {
			return R.drawable.weathericon_condition_07;
		} else if (weatherInfo.equals("С��") || weather.equals("С������")) {
			return R.drawable.weathericon_condition_08;
		} else if (weatherInfo.equals("����")) {
			return R.drawable.weathericon_condition_09;
		} else if (weatherInfo.equals("������")) {
			return R.drawable.weathericon_condition_10;
		} else if (weatherInfo.equals("Сѩ")) {
			return R.drawable.weathericon_condition_11;
		} else if (weatherInfo.equals("��ѩ")) {
			return R.drawable.weathericon_condition_12;
		} else if (weatherInfo.equals("���ѩ")) {
			return R.drawable.weathericon_condition_13;
		} else {
			return R.drawable.weathericon_condition_17;
		}
	}

	private void showWeather() {
		SharedPreferences pre = PreferenceManager
				.getDefaultSharedPreferences(WeatherActivity.this);
		title.setText(pre.getString("city_name", ""));
		temp1.setText(pre.getString("temp1", ""));
		temp2.setText(pre.getString("temp2", ""));
		publish.setText("����" + pre.getString("publish_time", "") + "����");
		// time.setText(pre.getString("current_time", ""));
		weather.setText(pre.getString("weather_desp", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		title.setVisibility(View.VISIBLE);

		String info = pre.getString("weather_desp", "");
		weather_icon = getWeatherIcon(info);
		image.setImageResource(weather_icon);

		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}

	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	private void queryWeather(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						String[] arry = response.split("\\|");
						if (arry != null && arry.length == 2) {
							String weatherCode = arry[1];
							queryWeather(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							showWeather();
						}
					});

				}

			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						publish.setText("ͬ��ʧ��...");

					}
				});

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.change:
			// Intent intent = new Intent(this, ChooseAreaActivity.class);

			SharedPreferences pre1 = PreferenceManager
					.getDefaultSharedPreferences(this);
			String cityName = pre1.getString("city_name", "");
			Intent intent = new Intent(this, CountySelectedActivity.class);
			intent.putExtra("from_weather_activity", true);
			intent.putExtra("cityName", cityName);
			Log.i("cityname", cityName);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh:
			publish.setText("����ͬ����...");
			SharedPreferences pre = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = pre.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeather(weatherCode);
			}
			break;

		default:
			break;
		}

	}
}
