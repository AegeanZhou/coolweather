package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.view.MyView;

import android.app.Activity;
import android.os.Bundle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.view.View;
import android.widget.LinearLayout;

public class ViewActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
//		LinearLayout rootLayout = (LinearLayout) findViewById(R.id.root);
//		final MyView draw = new MyView(this);
//		draw.setMinimumHeight(500);
//		draw.setMinimumWidth(300);
//		rootLayout.addView(draw);
	}

	// public class MyView extends View {
	// PathEffect effect = new PathEffect();
	// private int color = Color.WHITE;
	// private Paint paint;
	// Path path;
	//
	// public MyView(Context context) {
	// super(context);
	// paint = new Paint();
	// paint.setStyle(Paint.Style.STROKE);
	// paint.setStrokeWidth(4);
	// // 初始化Path
	// path = new Path();
	// // path的起点坐标
	// path.moveTo(0, 0);
	// path.lineTo(250, 100);
	// }
	//
	// @Override
	// protected void onDraw(Canvas canvas) {
	// effect = new CornerPathEffect(10);
	// // 将画布移到（8，8）处开始控制
	// canvas.translate(8, 8);
	// paint.setPathEffect(effect);
	// paint.setColor(color);
	// canvas.drawPath(path, paint);
	// canvas.translate(0, 60);
	// // super.onDraw(canvas);
	// invalidate();
	// }
	//
	// }

}
