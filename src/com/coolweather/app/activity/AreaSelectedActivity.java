package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.model.Area;
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

public class AreaSelectedActivity extends BaseActivity implements
		OnClickListener {
	private Button add;
	private Button back;
	private ListView selectedList;
	private List<County> list;
	private List<String> dataList = new ArrayList<String>();
	private CoolWeatherDB coolWeatherDB;
	private ArrayAdapter<String> adapter;

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
		String citynm = getIntent().getStringExtra("citynm");
		County county = coolWeatherDB.queryCounty(citynm);

		// 判断activity是否由点击add按钮后跳转过来的。是，则添加到数据库；不是，则不执行添加操作
		if (getIntent().getBooleanExtra("add", false)) {
			coolWeatherDB.saveSelectedCounty(county);
		}
		list = coolWeatherDB.loadSelected();

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		selectedList.setAdapter(adapter);
		querySelected();

		selectedList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String name = dataList.get(position);

				String weaid = "";
				List<Area> areaList = coolWeatherDB.loadArea();
				for (Area area : areaList) {
					if (name.equals(area.getCitynm())) {
						weaid = area.getWeaid();
					}
				}

				Intent intent = new Intent(AreaSelectedActivity.this,
						WeatherShowActivity.class);
				intent.putExtra("weaid", weaid);
				intent.putExtra("citynm", name);
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
						menu.add(0, 0, 0, "删除");

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
			if (selected != null) {
				dataList.add(selected);
			} else {
				int id = c.getId();
				// Log.i("getid", id + "");
				coolWeatherDB.delSelectedCounty(id);
			}
			// Log.i("querySelected", dataList.size() + "");
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			Intent i = new Intent(this, ChooseWhereActivity.class);
			startActivity(i);
			break;
		case R.id.add:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			startActivity(intent);
			break;
		}

	}

	/*
	 * 重写返回键按钮点击跳转到ChooseWhereActivity界面
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(AreaSelectedActivity.this,
				ChooseWhereActivity.class);
		startActivity(intent);
		super.onBackPressed();
	}

}
