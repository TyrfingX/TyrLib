package com.tyrlib2.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.tyrlib2.files.AndroidBitmap;
import com.tyrlib2.files.IBitmap;
import com.tyrlib2.graphics.text.GLText;
import com.tyrlib2.graphics.text.TextRenderer;

public class AndroidMedia extends Media {
	
	private Context context;
	
	public AndroidMedia(Context context) {
		this.context = context;
	}
	
	@Override
	public InputStream openAsset(String fileName) throws IOException {
		return context.getResources().getAssets().open(fileName);
	}

	@Override
	public FileInputStream openFileInput(String fileName) throws IOException {
		return context.openFileInput(fileName);
	}

	@Override
	public InputStream openRawResource(int id) throws IOException {
		return context.getResources().openRawResource(id);
	}

	@Override
	public IBitmap loadBitmap(int resID, boolean prescaling) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = prescaling;   // No pre-scaling
		return new AndroidBitmap(BitmapFactory.decodeResource(context.getResources(), resID, options));
	}

	@Override
	public int getResourceID(String source, String resType) {
		return context.getResources().getIdentifier(source, "drawable", context.getPackageName());
	}

	@Override
	public TextRenderer createTextRenderer(String fontSource, int size) {
		TextRenderer glText = new GLText(context.getAssets());
		glText.load( fontSource, size, 2, 2 );  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
		glText.setScale(1);
		return glText;
	}
	
	
}
