package com.tyrfing.games.tyrlib3;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.tyrfing.games.tyrlib3.model.files.IBitmap;
import com.tyrfing.games.tyrlib3.model.math.Vector2F;
import com.tyrfing.games.tyrlib3.model.sound.IMusic;
import com.tyrfing.games.tyrlib3.model.sound.ISound;
import com.tyrfing.games.tyrlib3.view.bitmap.ICanvas;
import com.tyrfing.games.tyrlib3.view.bitmap.IDrawableBitmap;
import com.tyrfing.games.tyrlib3.view.bitmap.IPaint;
import com.tyrfing.games.tyrlib3.view.bitmap.ITypeface;
import com.tyrfing.games.tyrlib3.view.graphics.text.IGLText;

public abstract class Media {
	public static Media CONTEXT;
	
	protected Map<Integer, Object> options = new HashMap<Integer, Object>();
	
	public static final int PRECISION = 0;
	public static final int DEPTH_TEXTURES_ENABLED = 1;
	
	public abstract InputStream openAsset(String fileName) throws IOException;
	public abstract FileInputStream openFileInput(String fileName) throws IOException;
	public abstract InputStream openRawResource(int id) throws IOException;
	public abstract IBitmap loadBitmap(int resID, boolean prescaling);
	public abstract void loadBitmap(IBitmap bitmap);
	public abstract IDrawableBitmap createAlphaBitmap(int width, int height );
	public abstract IDrawableBitmap createBitmap(int width, int height );
	public abstract int getResourceID(String source, String resType);
	public abstract IGLText createTextRenderer(String fontSource, int size);
	public abstract ITypeface createFromAsset(String file);
	public abstract IPaint createPaint(ICanvas canvas);
	public abstract ICanvas createCanvas();
	public abstract ICanvas createCanvas(IDrawableBitmap bitmap);
	public abstract Vector2F getScreenSize();
	public abstract void serializeTo(Serializable s, String target, String fileName);
	public abstract Object deserializeFrom(String target, String fileName);
	public abstract String getClipboard();
	public abstract IBitmap loadStaticBitmap(int resID, boolean prescaling);
	public abstract void quit();
	public abstract boolean fileExists(String target, String fileName);
	public abstract ISound createSound(String source);
	public abstract IMusic createMusic(String source);
	
	public Map<Integer, Object> getOptions() {
		return options;
	}
}
