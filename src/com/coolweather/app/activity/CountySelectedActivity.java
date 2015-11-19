package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class CountySelectedActivity extends Activity implements OnClickListener {
	private Button add;
	private Button back;
	private ListView selectedList;
	private List<County> list;
	private List<String> dataList = new ArrayList<String>();
	private CoolWeatherDB coolWeatherDB;
	private ArrayAdapter<String> adapter;
	private int flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.county_selected_list);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		add = (Button) findViewById(R.id.add);
		back = (Button) findViewById(R.id.back);
		add.setOnClickListener(this);
		back.setOnClickListener(this);
		selectedList = (ListView) findViewById(R.id.selected);

		list = new ArrayList<County>();
		String cityName = getIntent().getStringExtra("cityName");
		County county = coolWeatherDB.queryCounty(cityName);
		coolWeatherDB.saveSelectedCounty(county);
		list = coolWeatherDB.loadSelected();

		Log.i("countySelected", list.size() + "");

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		selectedList.setAdapter(adapter);
		querySelected();

		selectedList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				County county = list.get(position);
				String name = dataList.get(position);
				String countyCode = county.getCountyCode();
				String countyName = county.getCountyName();
				Intent intent = new Intent(CountySelectedActivity.this,
						WeatherActivity.class);
				intent.putExtra("county_code", countyCode);

				Log.i("countyselected", name + countyCode + countyName);

				startActivity(intent);

			}
		});
		ItemOnLongClick();
	}

	private void ItemOnLongClick() {
		selectedList
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu,
							View view, ContextMenuInfo arg2) {
						menu.add(0, 0, 0, "É¾³ý");

					}
				});

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int i = info.position;
		String name = dataList.get(i);
		switch (item.getItemId()) {
		case 0:
			coolWeatherDB.delSelectedCounty(name);
			dataList.remove(i);
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void querySelected() {
		for (County c : list) {
			String selected = c.getCountyName();
			dataList.add(selected);
			Log.i("querySelected", selected);
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			Intent i = new Intent(this, ChooseAreaActivity.class);
			startActivity(i);
			break;
		case R.id.add:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_county_selected_activity", true);
			startActivity(intent);
			break;
		}

	}

	@Override
	protected void onResume() {
		flag++;
		for (int i = 1; i < flag; i++)
			finish();
		Log.i("flag", flag + "");
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, WeatherActivity.class);
//		intent.addFlags(flag);
		startActivity(intent);
		finish();
		// super.onBackPressed();
	}

}
