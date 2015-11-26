package com.coolweather.app.managerTool;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityCollector {
	public static List<Activity> list = new ArrayList<Activity>();

	public static void addActivity(Activity activity) {
		list.add(activity);
	}

	public static void removeActivity(Activity activity) {
		list.remove(activity);
	}

	public static void finishAll() {
		for (Activity a : list) {
			if (!a.isFinishing()) {
				a.finish();
			}
		}
	}

}
