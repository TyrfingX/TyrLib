package com.tyrfing.games.tyrlib3.bitmap;

import com.tyrfing.games.tyrlib3.math.Rect;
import com.tyrfing.games.tyrlib3.util.Color;


public interface ICanvas {
	public void drawText(char[] text, int index, int count, float x, float y, IPaint paint);
	public void drawText(String s, float x, float y, IPaint paint);
	public void setBitmap(IDrawableBitmap bitmap);
	public void drawArc(Rect rect, float startDegree, float degree, boolean useCenter, IPaint paint);
	public void setRGB(int x, int y, Color color);
}
