package com.tyrlib2.bitmap;

public interface IDrawableBitmap {
	public int getWidth();
	public int getHeight();
	public void recycle();
	
	public void eraseColor(int color);
	public int toTexture();
}
