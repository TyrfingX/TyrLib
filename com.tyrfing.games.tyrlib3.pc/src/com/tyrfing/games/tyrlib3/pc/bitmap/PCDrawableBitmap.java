package com.tyrfing.games.tyrlib3.pc.bitmap;

import java.awt.image.BufferedImage;

import com.tyrfing.games.tyrlib3.bitmap.IDrawableBitmap;
import com.tyrfing.games.tyrlib3.files.IBitmap;
import com.tyrfing.games.tyrlib3.pc.main.PCBitmap;

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
