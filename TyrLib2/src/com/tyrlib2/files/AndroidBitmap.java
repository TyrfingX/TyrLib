package com.tyrlib2.files;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import com.tyrlib2.graphics.renderer.TyrGL;

public class AndroidBitmap implements IBitmap {
	
	private Bitmap bitmap;
	private int handle;
	private int[] pixels;
	
	private int width;
	private int height;

	public AndroidBitmap(Bitmap bitmap, int handle) {
		this.bitmap = bitmap;
		this.handle = handle;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}
	
	public AndroidBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}
	
	private void loadPixels() {
		pixels = new int[width*height];
		bitmap.getPixels(pixels,0,width,0,0,width,height);
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void bind() {
		GLUtils.texImage2D(TyrGL.GL_TEXTURE_2D, 0, bitmap, 0);
	}

	@Override
	public void recycle() {
		bitmap.recycle();
	}

	@Override
	public int getHandle() {
		return handle;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void setHanlde(int handle) {
		this.handle = handle;
	}

	@Override
	public int getRGB(int x, int y) {
		if (pixels == null) {
			loadPixels();
		}
		return pixels[x+y*width];
	}
	
}
