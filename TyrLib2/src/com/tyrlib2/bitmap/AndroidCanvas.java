package com.tyrlib2.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.tyrlib2.math.Rect;

public class AndroidCanvas implements ICanvas {
	private Canvas canvas;
	
	public AndroidCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public void drawText(char[] text, int index, int count, float x, float y, IPaint paint) {
		Paint p = ((AndroidPaint) paint).paint;
		canvas.drawText(text, index, count, x, y, p);
	}
	
	@Override
	public void drawText(String s, float x, float y, IPaint paint) {
		Paint p = ((AndroidPaint) paint).paint;
		canvas.drawText(s, x, y, p);
	}

	@Override
	public void setBitmap(IDrawableBitmap bitmap) {
		Bitmap b = ((AndroidDrawableBitmap)bitmap).getBitmap();
		canvas.setBitmap(b);
	}

	@Override
	public void drawArc(Rect rect, float startDegree, float degree, boolean useCenter, IPaint paint) {
		AndroidPaint p = (AndroidPaint) paint;
		RectF r = new RectF(rect.left, rect.top, rect.right, rect.bottom);
		canvas.drawArc(r, startDegree, degree, useCenter, p.paint);
	}


}
