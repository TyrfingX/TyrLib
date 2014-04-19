package com.tyrlib2.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.tyrlib2.bitmap.ICanvas;
import com.tyrlib2.bitmap.IDrawableBitmap;
import com.tyrlib2.bitmap.IPaint;
import com.tyrlib2.bitmap.ITypeface;
import com.tyrlib2.files.IBitmap;
import com.tyrlib2.graphics.text.IGLText;
import com.tyrlib2.math.Vector2;

public abstract class Media {
	public static Media CONTEXT;
	
	public abstract InputStream openAsset(String fileName) throws IOException;
	public abstract FileInputStream openFileInput(String fileName) throws IOException;
	public abstract InputStream openRawResource(int id) throws IOException;
	public abstract IBitmap loadBitmap(int resID, boolean prescaling);
	public abstract IDrawableBitmap createAlphaBitmap(int width, int height );
	public abstract int getResourceID(String source, String resType);
	public abstract IGLText createTextRenderer(String fontSource, int size);
	public abstract ITypeface createFromAsset(String file);
	public abstract IPaint createPaint(ICanvas canvas);
	public abstract ICanvas createCanvas();
	public abstract Vector2 getScreenSize();
}
