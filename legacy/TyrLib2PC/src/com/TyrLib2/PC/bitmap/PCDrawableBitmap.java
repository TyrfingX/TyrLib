package com.TyrLib2.PC.bitmap;

import java.awt.image.BufferedImage;

import com.TyrLib2.PC.main.PCBitmap;
import com.tyrlib2.bitmap.IDrawableBitmap;
import com.tyrlib2.files.IBitmap;

public class PCDrawableBitmap implements IDrawableBitmap {

	public final BufferedImage canvas;
	
	public PCDrawableBitmap(BufferedImage canvas) {
		this.canvas = canvas;
	}
	
	@Override
	public int getWidth() {
		return canvas.getWidth();
	}

	@Override
	public int getHeight() {
		return canvas.getHeight();
	}

	@Override
	public void recycle() {
		canvas.flush();
	}

	@Override
	public void eraseColor(int color) {
	
	}

	@Override
	public int toTexture() {
		PCBitmap pcbitmap = new PCBitmap(canvas);
		return pcbitmap.getHandle();
	}
	
	@Override
	public IBitmap toBitmap() {
		PCBitmap pcbitmap = new PCBitmap(canvas);
		return pcbitmap;
	}

}
