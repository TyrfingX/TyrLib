package com.tyrfing.games.tyrlib3.files;

public interface IBitmap {
	public int getWidth();
	public int getHeight();
	public void bind();
	public void recycle();
	public int getHandle();
	public int getRGB(int x, int y);
	public void toTexture();
}
