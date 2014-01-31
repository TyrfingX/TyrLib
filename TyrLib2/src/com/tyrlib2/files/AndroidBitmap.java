package com.tyrlib2.files;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import com.tyrlib2.graphics.renderer.TyrGL;

public class AndroidBitmap implements IBitmap {
	
	private Bitmap bitmap;

	public AndroidBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	@Override
	public int getWidth() {
		return bitmap.getWidth();
	}

	@Override
	public int getHeight() {
		return bitmap.getHeight();
	}

	@Override
	public void bind() {
		GLUtils.texImage2D(TyrGL.GL_TEXTURE_2D, 0, bitmap, 0);
	}

	@Override
	public void recycle() {
		bitmap.recycle();
	}
	
}
