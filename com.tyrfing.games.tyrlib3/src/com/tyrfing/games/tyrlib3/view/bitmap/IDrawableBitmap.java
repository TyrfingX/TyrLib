package com.tyrfing.games.tyrlib3.view.bitmap;

import com.tyrfing.games.tyrlib3.model.files.IBitmap;

public interface IDrawableBitmap {
	public int getWidth();
	public int getHeight();
	public void recycle();
	
	public void eraseColor(int color);
	public int toTexture();
	public IBitmap toBitmap();
}
