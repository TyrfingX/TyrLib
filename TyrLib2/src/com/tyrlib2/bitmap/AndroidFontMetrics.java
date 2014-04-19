package com.tyrlib2.bitmap;

import android.graphics.Paint;

public class AndroidFontMetrics extends IFontMetrics {
	public final Paint.FontMetrics fm;
	
	public AndroidFontMetrics(Paint.FontMetrics fm) {
		this.fm  = fm;
		
		this.top = fm.top;
		this.bottom = fm.bottom;
		this.ascent = fm.ascent;
		this.descent = fm.descent;
	}
}
