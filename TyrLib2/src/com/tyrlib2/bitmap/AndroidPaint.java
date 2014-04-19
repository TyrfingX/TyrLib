package com.tyrlib2.bitmap;

import android.graphics.Paint;

public class AndroidPaint implements IPaint {

	public final Paint paint = new Paint();
	
	@Override
	public void setAntiAlias(boolean state) {
		paint.setAntiAlias(state);
	}

	@Override
	public void setTextSize(int size) {
		paint.setTextSize(size);
	}

	@Override
	public void setColor(int color) {
		paint.setColor(color);
	}

	@Override
	public void setTypeface(ITypeface typeface) {
		paint.setTypeface(((AndroidTypeface)typeface).tf);
	}

	@Override
	public IFontMetrics getFontMetrics() {
		return new AndroidFontMetrics(paint.getFontMetrics());
	}

	@Override
	public void getTextWidths(char[] s, int index, int count, float[] widths) {
		paint.getTextWidths(s, index, count, widths);
	}

}
