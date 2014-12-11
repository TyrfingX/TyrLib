package com.tyrlib2.bitmap;

import com.tyrlib2.math.Rect;
import com.tyrlib2.util.Color;


public interface ICanvas {
	public void drawText(char[] text, int index, int count, float x, float y, IPaint paint);
	public void drawText(String s, float x, float y, IPaint paint);
	public void setBitmap(IDrawableBitmap bitmap);
	public void drawArc(Rect rect, float startDegree, float degree, boolean useCenter, IPaint paint);
	public void setRGB(int x, int y, Color color);
}
