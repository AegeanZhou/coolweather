package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.coolweather.app.R;
import com.coolweather.app.managerTool.ActivityCollector;
import com.coolweather.app.model.Area;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ChooseWhereActivity extends BaseActivity {
	private SearchView search;
	private CoolWeatherDB coolWeatherDB;
	private List<Area> areaList;
	private GridView grid;
	private static String[] id = { "1", "36", "165", "169", "147", "446",
			"209", "222", "219", "399", "248", "425" };
	private static String[] cityNm = { "北京", "上海", "广州", "深圳", "厦门", "大连",
			"昆明", "丽江", "大理", "南京", "武汉", "长春" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_where);
		search = (SearchView) findViewById(R.id.search);
		grid = (GridView) findViewById(R.id.hot_city);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		search.setIconifiedByDefault(false);
		search.setSubmitButtonEnabled(true);
		search.setQueryHint("请输入要查询的城市");
		search.setBackgroundResource(R.drawable.scbg);
		search.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				String name = search.getQuery().toString();
				if (name != null) {
					String weaid = queryAreaName(name);
					if (weaid != "") {
						Intent i = new Intent(ChooseWhereActivity.this,
								WeatherShowActivity.class);
						i.putExtra("weaid", weaid);
						i.putExtra("citynm", name);
						startActivity(i);
					} else {

					}
					;
				}
				return true;
			}

		});

		// 热门城市用gridview显示，用map<String，object>来存储信息
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < cityNm.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("city", cityNm[i]);
			listItems.add(listItem);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, listItems,
				R.layout.cell, new String[] { "city" },
				new int[] { R.id.choose });
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// textBtn.setTextColor(Color.RED);
				Intent intent = new Intent(ChooseWhereActivity.this,
						WeatherShowActivity.class);
				intent.putExtra("citynm", cityNm[position]);
				intent.putExtra("weaid", id[position]);
				startActivity(intent);

			}
		});
	}

	public String queryAreaName(String name) {
		String weaid = "";
		areaList = coolWeatherDB.loadArea();
		if (areaList.size() > 0) {
			for (Area area : areaList) {
				if (name.equals(area.getCitynm())) {
					weaid = area.getWeaid();
				}
			}
		} else {
			queryAreaFromServer(name);
		}

		// Log.i("input", name + "**" + weaid + ")))" + areaList.size() +
		// "&&&");
		return weaid;

	}

	public void queryAreaFromServer(final String name) {
		String address = "http://api.k780.com:88/?app=weather.city&&appkey=16510&sign=66e60f4cc33687482ceff6391355527e&format=json";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				Utility.handleAreaRequest(coolWeatherDB, response);
				queryAreaName(name);

			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void onBackPressed() {
		exitDialog();
	}

	// 退出前弹出确认对话框
	private void exitDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("是否退出程序");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				ActivityCollector.finishAll();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

}
