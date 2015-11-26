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
			dateToday.setText("ͬ����...");
			queryWeather(weaid);
		} else {
			showWeather();
		}
	}

	// ��������ʾ��������
	private void showWeather() {
		SharedPreferences pre = PreferenceManager
				.getDefaultSharedPreferences(WeatherShowActivity.this);
		pos = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
		String date = sdf.format(new Date());
		dateToday.setText(date);
		weatherText.setText(pre.getString("weather", ""));
		tempNow.setText(pre.getString("tempNow", "") + "��");
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

		// ��ʼ��
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
				publishProgress(); // ֪ͨȥ����UI
			}

			return null;
		}

		public void onProgressUpdate(Void... voids) {
			if (isCancelled())
				return;
			// ����UI
			adapter.notifyDataSetChanged();
		}
	}

	// ͨ�������weaid����ҳ�ϲ�ѯ����
	private void queryWeather(String weaid) {
		String address = "http://api.k780.com:88/?app=weather.future&weaid="
				+ weaid
				+ "&&appkey=16510&sign=66e60f4cc33687482ceff6391355527e&format=json";
		queryFromServer(address);
	}

	// ͨ���ṩ����ַ��ѯ�ӿ�
	private void queryFromServer(String address) {
		Log.i("queryFromServer", address);
		// ������ҳ����
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			// �ص��ӿڷ�����д�������صĽ�����ύ������������
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
		if (weatherInfo.equals("��") || weatherInfo.contains("��")) {
			return R.drawable.w00;
		} else if (weatherInfo.equals("����") || weatherInfo.contains("����")) {
			return R.drawable.w01;
		} else if (weatherInfo.equals("��") || weatherInfo.contains("��")) {
			return R.drawable.w02;
		} else if (weatherInfo.equals("����") || weatherInfo.contains("����")) {
			return R.drawable.w03;
		} else if (weatherInfo.equals("������") || weatherInfo.contains("������")) {
			return R.drawable.w04;
		} else if (weatherInfo.equals("�������б���")
				|| weatherInfo.contains("�������б���")) {
			return R.drawable.w05;
		} else if (weatherInfo.equals("���ѩ") || weatherInfo.contains("���ѩ")) {
			return R.drawable.w06;
		} else if (weatherInfo.equals("С��") || weatherInfo.contains("С��")) {
			return R.drawable.w07;
		} else if (weatherInfo.equals("����") || weatherInfo.contains("����")) {
			return R.drawable.w08;
		} else if (weatherInfo.equals("����") || weatherInfo.contains("����")) {
			return R.drawable.w09;
		} else if (weatherInfo.equals("����") || weatherInfo.contains("����")) {
			return R.drawable.w10;
		} else if (weatherInfo.equals("����") || weatherInfo.contains("����")) {
			return R.drawable.w11;
		} else if (weatherInfo.equals("�ش���") || weatherInfo.contains("�ش���")) {
			return R.drawable.w12;
		} else if (weatherInfo.equals("��ѩ") || weatherInfo.contains("��ѩ")) {
			return R.drawable.w13;
		} else if (weatherInfo.equals("Сѩ") || weatherInfo.equals("Сѩ")) {
			return R.drawable.w14;
		} else if (weatherInfo.equals("��ѩ") || weatherInfo.equals("��ѩ")) {
			return R.drawable.w15;
		} else if (weatherInfo.equals("��ѩ") || weatherInfo.equals("��ѩ")) {
			return R.drawable.w16;
		} else if (weatherInfo.equals("��ѩ") || weatherInfo.equals("��ѩ")) {
			return R.drawable.w17;
		} else if (weatherInfo.equals("��") || weatherInfo.equals("��")) {
			return R.drawable.w18;
		} else if (weatherInfo.equals("����") || weatherInfo.equals("����")) {
			return R.drawable.w19;
		} else if (weatherInfo.equals("ɳ����") || weatherInfo.equals("ɳ����")) {
			return R.drawable.w20;
		} else if (weatherInfo.equals("С��-����") || weatherInfo.equals("С��-����")) {
			return R.drawable.w21;
		} else if (weatherInfo.equals("����-����") || weatherInfo.equals("����-����")) {
			return R.drawable.w22;
		} else if (weatherInfo.equals("����-����") || weatherInfo.equals("����-����")) {
			return R.drawable.w23;
		} else if (weatherInfo.equals("����-����") || weatherInfo.equals("����-����")) {
			return R.drawable.w24;
		} else if (weatherInfo.equals("����-�ش���")
				|| weatherInfo.equals("����-�ش���")) {
			return R.drawable.w25;
		} else if (weatherInfo.equals("Сѩ-��ѩ") || weatherInfo.equals("Сѩ-��ѩ")) {
			return R.drawable.w26;
		} else if (weatherInfo.equals("��ѩ-��ѩ") || weatherInfo.equals("��ѩ-��ѩ")) {
			return R.drawable.w27;
		} else if (weatherInfo.equals("��ѩ-��ѩ") || weatherInfo.equals("��ѩ-��ѩ")) {
			return R.drawable.w28;
		} else if (weatherInfo.equals("����") || weatherInfo.equals("����")) {
			return R.drawable.w29;
		} else if (weatherInfo.equals("��ɳ") || weatherInfo.equals("��ɳ")) {
			return R.drawable.w30;
		} else if (weatherInfo.equals("ǿɳ����") || weatherInfo.equals("ǿɳ����")) {
			return R.drawable.w31;
		} else if (weatherInfo.equals("��") || weatherInfo.equals("��")) {
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
			dateToday.setText("����ͬ����...");
			queryWeather(weaid);

			break;

		default:
			break;
		}

	}

	// ��д���ؼ����Ӹ�ҳ����ת��ChooseWhereActivityҳ��
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(WeatherShowActivity.this,
				ChooseWhereActivity.class);
		startActivity(intent);
		super.onBackPressed();
	}

}
