package com.coolweather.app.Adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.coolweather.app.R;
import com.coolweather.app.model.ImageEntity;

import android.content.Context;
import android.content.Entity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<ImageEntity> list;
	private String[] tempList;
	private String[] dateList;

	public ImageAdapter(Context context, ArrayList<ImageEntity> list,
			String[] dateList, String[] tempList) {
		this.mContext = context;
		this.list = list;
		this.dateList = dateList;
		this.tempList = tempList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// View view;
		// if (convertView == null) {
		// view = LayoutInflater.from(mContext).inflate(R.layout.grid_cell,
		// null);
		// } else
		// view = convertView;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		convertView = inflater.inflate(R.layout.grid_cell, null);
		ImageView imgView = (ImageView) convertView
				.findViewById(R.id.weather_for);
		TextView date = (TextView) convertView.findViewById(R.id.date_time);
		TextView temp = (TextView) convertView.findViewById(R.id.temp);
		date.setText(dateList[position]);

		ImageEntity b = list.get(position);

		imgView.setImageBitmap(b.getImage());
		// 如果有图片则读取，没有则跳过
		if (list.get(position) != null) {
			imgView.setImageBitmap(b.getImage());
			date.setText(dateList[position]);
			temp.setText(tempList[position]);
		}
		return convertView;
	}

}
