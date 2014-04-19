package com.tyrlib2.bitmap;


public interface IPaint {
	public void setAntiAlias(boolean state);
	public void setTextSize(int size);
	public void setColor(int color);
	public void setTypeface(ITypeface typeface);
	public IFontMetrics getFontMetrics();
	public void getTextWidths(char[] s, int index, int count, float[] widths);
}
