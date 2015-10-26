package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	private static final int LEVEL_PROVINCE = 0;
	private static final int LEVEL_CITY = 1;
	private static final int LEVEL_COUNTY = 2;
	public TextView title;
	public ListView list;
	private ArrayAdapter<String> adapter;
	private List<String> dataList = new ArrayList<String>();
	private List<Province> provincesList;
	private List<City> citiesList;
	private List<County> countiesList;
	private CoolWeatherDB coolWeatherDB;
	private int currentLevel;
	private Province selectedProvince;
	private City selectedCity;
	private County selectedCounty;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		title = (TextView) findViewById(R.id.title);
		list = (ListView) findViewById(R.id.list);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		list.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provincesList.get(index);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = citiesList.get(index);
					queryCounties();
				}

			}

		});
		queryProvinces();
	}

	private void queryProvinces() {
		provincesList = coolWeatherDB.loadProvinces();
		if (provincesList.size() > 0) {
			dataList.clear();
			for (Province province : provincesList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			list.setSelection(0);
			title.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}

	}

	private void queryCities() {
		citiesList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (citiesList.size() > 0) {
			dataList.clear();
			for (City city : citiesList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			list.setSelection(0);
			title.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}

	}

	private void queryCounties() {
		countiesList = coolWeatherDB.loadCounty(selectedCity.getId());
		if (countiesList.size() > 0) {
			dataList.clear();
			for (County county : countiesList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			list.setSelection(0);
			title.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}

	}

	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvinceResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCityResponse(response,
							coolWeatherDB, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountyResponse(response,
							coolWeatherDB, selectedCity.getId());
				}
				if (result) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
								Log.i("main", "county");
							}

						}

					});

				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败",
								Toast.LENGTH_SHORT).show();

					}
				});

			}
		});
	}

	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}

	}

	private void showProgressDialog() {
		if (progressDialog != null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("正在加载中...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

	}

	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}
}
