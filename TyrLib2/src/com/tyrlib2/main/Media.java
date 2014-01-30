package com.tyrlib2.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.BitmapFactory;

import com.tyrlib2.files.IBitmap;
import com.tyrlib2.graphics.text.TextRenderer;

public abstract class Media {
	public static Media CONTEXT;
	
	public abstract InputStream openAsset(String fileName) throws IOException;
	public abstract FileInputStream openFileInput(String fileName) throws IOException;
	public abstract InputStream openRawResource(int id) throws IOException;
	public abstract IBitmap loadBitmap(int resID, boolean prescaling);
	public abstract int getResourceID(String source, String resType);
	public abstract TextRenderer createTextRenderer(String fontSource, int size);
}
