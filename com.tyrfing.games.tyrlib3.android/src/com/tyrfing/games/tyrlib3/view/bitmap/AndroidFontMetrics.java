package com.tyrfing.games.tyrlib3.view.bitmap;

import com.tyrfing.games.tyrlib3.view.bitmap.IFontMetrics;

import android.graphics.Paint;

public class AndroidFontMetrics extends IFontMetrics {
	public final Paint.FontMetrics fm;
	
	public AndroidFontMetrics(Paint.FontMetrics fm) {
		this.fm  = fm;
		
		this.top = fm.top;
		this.bottom = fm.bottom * 2;
		this.ascent = fm.ascent;
		this.descent = fm.descent;
	}
}
