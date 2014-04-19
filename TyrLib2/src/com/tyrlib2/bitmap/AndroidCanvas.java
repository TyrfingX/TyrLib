package com.tyrlib2.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

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
	public void setBitmap(IDrawableBitmap bitmap) {
		Bitmap b = ((AndroidDrawableBitmap)bitmap).getBitmap();
		canvas.setBitmap(b);
	}
}
