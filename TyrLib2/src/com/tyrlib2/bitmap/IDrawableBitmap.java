package com.tyrlib2.bitmap;

import com.tyrlib2.files.IBitmap;

public interface IDrawableBitmap {
	public int getWidth();
	public int getHeight();
	public void recycle();
	
	public void eraseColor(int color);
	public int toTexture();
	public IBitmap toBitmap();
}
