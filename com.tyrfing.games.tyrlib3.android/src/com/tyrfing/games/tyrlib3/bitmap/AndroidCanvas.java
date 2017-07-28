package com.tyrfing.games.tyrlib3.bitmap;

import com.tyrfing.games.tyrlib3.bitmap.ICanvas;
import com.tyrfing.games.tyrlib3.bitmap.IDrawableBitmap;
import com.tyrfing.games.tyrlib3.bitmap.IPaint;
import com.tyrfing.games.tyrlib3.math.Rect;
import com.tyrfing.games.tyrlib3.util.Color;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class AndroidCanvas implements ICanvas {
	private Canvas canvas;
	private AndroidPaint paint;
	
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

	@Override
	public void setRGB(int x, int y, Color color) {
		if (paint == null) {
			paint = new AndroidPaint();
		}
		
		paint.setColor(color);
		canvas.drawPoint(x, y, paint.paint);
	}


}
