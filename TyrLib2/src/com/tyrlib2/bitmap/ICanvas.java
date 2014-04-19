package com.tyrlib2.bitmap;


public interface ICanvas {
	public void drawText(char[] text, int index, int count, float x, float y, IPaint paint);
	public void setBitmap(IDrawableBitmap bitmap);
}
