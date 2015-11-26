package com.coolweather.app.view;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PathEffect;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class MyView extends View {
	PathEffect effect = new PathEffect();
	private int color = Color.WHITE;
	private Paint paint;
	Path path;
	Path pathl;

	public MyView(Context context, List<String> list) {
		super(context);
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(4);
		// 初始化Path
		path = new Path();
		pathl = new Path();
		// path的起点坐标
		path.moveTo(0, 20);
		pathl.moveTo(0, 60);
		// 获取屏幕宽度
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();

		if (list.size() > 0) {
			// 要绘制的点的坐标
			for (int i = 0; i < list.size(); i++) {
				String[] temp = list.get(i).split("#");
				int high = Integer.parseInt(temp[0]);
				int low = Integer.parseInt(temp[1]);
				path.lineTo(width / 7 * (i + 1) - 80, (-high) * 2 + 75);
				path.addCircle(width / 7 * (i + 1) - 80, (-high) * 2 + 75, 5,
						Direction.CW);
				pathl.lineTo(width / 7 * (i + 1) - 80, (-low) * 2 + 70);
				pathl.addCircle(width / 7 * (i + 1) - 80, (-low) * 2 + 70, 5,
						Direction.CW);
			}
			path.lineTo(width, 20);
			pathl.lineTo(width, 10);
		} else {
			path.lineTo(200, 200);
			// Log.i("MyView", "list.size()!=0");
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		effect = new CornerPathEffect(10);
		// 将画布移到（8，8）处开始控制
		canvas.translate(8, 8);
		// canvas.drawColor(Color.BLUE);
		paint.setPathEffect(effect);
		paint.setColor(color);
		canvas.drawPath(path, paint);
		canvas.translate(0, 60);
		canvas.drawPath(pathl, paint);
	}

}
