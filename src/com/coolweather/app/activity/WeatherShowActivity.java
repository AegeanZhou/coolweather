package com.coolweather.app.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.coolweather.app.R;
import com.coolweather.app.Adapter.ImageAdapter;
import com.coolweather.app.model.ImageEntity;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.coolweather.app.view.MyView;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherShowActivity extends BaseActivity implements
		OnClickListener {
	private ImageView weatherIcon;
	private TextView weatherText;
	private TextView dateToday;
	private TextView tempNow;
	private TextView wind;
	private TextView windLevel;
	private List<String> pos;
	private int weather_icon = 0;
	String[] imagesUrl = new String[7];
	String[] weather_p = new String[7];
	String[] temp_p = new String[7];

	private GridView grid;
	private Button mainBtn;
	private Button refreshBtn;
	private TextView titleText;
	private String citynm;
	private String weaid;
	private boolean isAdd;
	private ArrayList<ImageEntity> list = new ArrayList<ImageEntity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_show);
		init();
		pos = new ArrayList<String>();
		// url = new ArrayList<String>();
		Intent intent = getIntent();
		citynm = intent.getStringExtra("citynm");
		titleText.setText(citynm);
		isAdd = intent.getBooleanExtra("add", false);

		weaid = intent.getStringExtra("weaid");
		if (!TextUtils.isEmpty(weaid)) {
			dateToday.setText("同步中...");
			queryWeather(weaid);
		} else {
			showWeather();
		}
	}

	// 将天气显示到界面上
	private void showWeather() {
		SharedPreferences pre = PreferenceManager
				.getDefaultSharedPreferences(WeatherShowActivity.this);
		pos = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		String date = sdf.format(new Date());
		dateToday.setText(date);
		weatherText.setText(pre.getString("weather", ""));
		tempNow.setText(pre.getString("tempNow", "") + "°");
		wind.setText(pre.getString("wind", ""));
		windLevel.setText(pre.getString("winp", ""));
		weather_icon = getWeatherIcon(pre.getString("weather", ""));
		weatherIcon.setBackgroundResource(weather_icon);
		String[] s = { "0", "1", "2", "3", "4", "5", "6" };
		for (int i = 0; i < 7; i++) {
			String key = s[i];
			String str = pre.getString(key, "");
			String[] res = str.split("#");
			Log.i("weatherShow", str + "str");
			pos.add(str);
			// url.add(res[2]);
			imagesUrl[i] = res[2];
			weather_p[i] = res[3];
			temp_p[i] = res[4];
		}

		LinearLayout rootLayout = (LinearLayout) findViewById(R.id.bottom);
		final MyView draw = new MyView(this, pos);
		draw.setMinimumHeight(500);
		draw.setMinimumWidth(300);
		rootLayout.setBackgroundColor(color.transparent);
		rootLayout.addView(draw);

	}

	public void addImage() {
		for (int i = 0; i < imagesUrl.length; i++) {
			ImageEntity b = new ImageEntity();
			if (list.size() != imagesUrl.length) {
				b.setImage(BitmapFactory.decodeResource(getResources(),
						R.drawable.ic_launcher));

				list.add(b);
			}
		}
		ImageAdapter adapter = new ImageAdapter(getApplicationContext(), list,
				weather_p, temp_p);
		grid.setAdapter(adapter);
		new ImageLoadTask(getApplicationContext(), adapter).execute(imagesUrl);
	}

	public class ImageLoadTask extends AsyncTask<String, Void, Void> {
		private ImageAdapter adapter;

		// 初始化
		public ImageLoadTask(Context context, ImageAdapter adapter) {
			this.adapter = adapter;
		}

		@Override
		protected Void doInBackground(String... params) {

			String p2 = params[1];
			for (int i = 0; i < adapter.getCount(); i++) {
				String url = params[i];
				ImageEntity bean = (ImageEntity) adapter.getItem(i);
				Bitmap bitmap = BitmapFactory.decodeStream(HttpUtil
						.HandlerData(url));
				bean.setImage(bitmap);
				publishProgress(); // 通知去更新UI
			}

			return null;
		}

		public void onProgressUpdate(Void... voids) {
			if (isCancelled())
				return;
			// 更新UI
			adapter.notifyDataSetChanged();
		}
	}

	// 通过传入的weaid从网页上查询天气
	private void queryWeather(String weaid) {
		String address = "http://api.k780.com:88/?app=weather.future&weaid="
				+ weaid
				+ "&&appkey=16510&sign=66e60f4cc33687482ceff6391355527e&format=json";
		queryFromServer(address);
	}

	// 通过提供的网址查询接口
	private void queryFromServer(String address) {
		Log.i("queryFromServer", address);
		// 发送网页请求
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			// 回调接口方法重写，处理返回的结果，提交给分析器分析
			@Override
			public void onFinish(String response) {
				Utility.handleWeatherResponseT(WeatherShowActivity.this,
						response);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showWeather();
						addImage();
					}
				});

			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void init() {
		weatherIcon = (ImageView) findViewById(R.id.wea_icon);
		weatherText = (TextView) findViewById(R.id.weather_text);
		dateToday = (TextView) findViewById(R.id.date);
		tempNow = (TextView) findViewById(R.id.temp);
		wind = (TextView) findViewById(R.id.wind_today);
		windLevel = (TextView) findViewById(R.id.wind_l);
		grid = (GridView) findViewById(R.id.grid);
		mainBtn = (Button) findViewById(R.id.main_btn);
		refreshBtn = (Button) findViewById(R.id.refresh_btn);
		titleText = (TextView) findViewById(R.id.title_text);

		mainBtn.setOnClickListener(this);
		refreshBtn.setOnClickListener(this);
	}

	private int getWeatherIcon(String weatherInfo) {
		if (weatherInfo.equals("晴") || weatherInfo.contains("晴")) {
			return R.drawable.w00;
		} else if (weatherInfo.equals("多云") || weatherInfo.contains("多云")) {
			return R.drawable.w01;
		} else if (weatherInfo.equals("阴") || weatherInfo.contains("阴")) {
			return R.drawable.w02;
		} else if (weatherInfo.equals("阵雨") || weatherInfo.contains("阵雨")) {
			return R.drawable.w03;
		} else if (weatherInfo.equals("雷阵雨") || weatherInfo.contains("雷阵雨")) {
			return R.drawable.w04;
		} else if (weatherInfo.equals("雷阵雨有冰雹")
				|| weatherInfo.contains("雷阵雨有冰雹")) {
			return R.drawable.w05;
		} else if (weatherInfo.equals("雨夹雪") || weatherInfo.contains("雨夹雪")) {
			return R.drawable.w06;
		} else if (weatherInfo.equals("小雨") || weatherInfo.contains("小雨")) {
			return R.drawable.w07;
		} else if (weatherInfo.equals("中雨") || weatherInfo.contains("中雨")) {
			return R.drawable.w08;
		} else if (weatherInfo.equals("大雨") || weatherInfo.contains("大雨")) {
			return R.drawable.w09;
		} else if (weatherInfo.equals("暴雨") || weatherInfo.contains("暴雨")) {
			return R.drawable.w10;
		} else if (weatherInfo.equals("大暴雨") || weatherInfo.contains("大暴雨")) {
			return R.drawable.w11;
		} else if (weatherInfo.equals("特大暴雨") || weatherInfo.contains("特大暴雨")) {
			return R.drawable.w12;
		} else if (weatherInfo.equals("阵雪") || weatherInfo.contains("阵雪")) {
			return R.drawable.w13;
		} else if (weatherInfo.equals("小雪") || weatherInfo.equals("小雪")) {
			return R.drawable.w14;
		} else if (weatherInfo.equals("中雪") || weatherInfo.equals("中雪")) {
			return R.drawable.w15;
		} else if (weatherInfo.equals("大雪") || weatherInfo.equals("大雪")) {
			return R.drawable.w16;
		} else if (weatherInfo.equals("暴雪") || weatherInfo.equals("暴雪")) {
			return R.drawable.w17;
		} else if (weatherInfo.equals("雾") || weatherInfo.equals("雾")) {
			return R.drawable.w18;
		} else if (weatherInfo.equals("冻雨") || weatherInfo.equals("冻雨")) {
			return R.drawable.w19;
		} else if (weatherInfo.equals("沙尘暴") || weatherInfo.equals("沙尘暴")) {
			return R.drawable.w20;
		} else if (weatherInfo.equals("小雨-中雨") || weatherInfo.equals("小雨-中雨")) {
			return R.drawable.w21;
		} else if (weatherInfo.equals("中雨-大雨") || weatherInfo.equals("中雨-大雨")) {
			return R.drawable.w22;
		} else if (weatherInfo.equals("大雨-暴雨") || weatherInfo.equals("大雨-暴雨")) {
			return R.drawable.w23;
		} else if (weatherInfo.equals("暴雨-大暴雨") || weatherInfo.equals("暴雨-大暴雨")) {
			return R.drawable.w24;
		} else if (weatherInfo.equals("大暴雨-特大暴雨")
				|| weatherInfo.equals("大暴雨-特大暴雨")) {
			return R.drawable.w25;
		} else if (weatherInfo.equals("小雪-中雪") || weatherInfo.equals("小雪-中雪")) {
			return R.drawable.w26;
		} else if (weatherInfo.equals("中雪-大雪") || weatherInfo.equals("中雪-大雪")) {
			return R.drawable.w27;
		} else if (weatherInfo.equals("大雪-暴雪") || weatherInfo.equals("大雪-暴雪")) {
			return R.drawable.w28;
		} else if (weatherInfo.equals("浮尘") || weatherInfo.equals("浮尘")) {
			return R.drawable.w29;
		} else if (weatherInfo.equals("扬沙") || weatherInfo.equals("扬沙")) {
			return R.drawable.w30;
		} else if (weatherInfo.equals("强沙尘暴") || weatherInfo.equals("强沙尘暴")) {
			return R.drawable.w31;
		} else if (weatherInfo.equals("霾") || weatherInfo.equals("霾")) {
			return R.drawable.w53;
		} else {
			return R.drawable.undefined;
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.main_btn:

			Intent intent = new Intent(this, AreaSelectedActivity.class);
			intent.putExtra("add", isAdd);
			intent.putExtra("citynm", citynm);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_btn:
			dateToday.setText("正在同步中...");
			queryWeather(weaid);

			break;

		default:
			break;
		}

	}

	// 重写返回键，从该页面跳转到ChooseWhereActivity页面
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(WeatherShowActivity.this,
				ChooseWhereActivity.class);
		startActivity(intent);
		super.onBackPressed();
	}

}
