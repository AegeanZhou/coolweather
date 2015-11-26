package com.coolweather.app.activity;

import com.coolweather.app.managerTool.ActivityCollector;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {
	// 创建activity时添加到列表
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
	}

	// 销毁时从列表移除
	@Override
	protected void onDestroy() {
		ActivityCollector.removeActivity(this);
		super.onDestroy();
	}
}
