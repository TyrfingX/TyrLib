package com.TyrLib2.PC.bitmap;

import java.awt.FontMetrics;

import com.tyrlib2.bitmap.IFontMetrics;


public class PCFontMetric extends IFontMetrics {
	private final FontMetrics fm;
	
	public PCFontMetric(FontMetrics fm) {
		this.fm = fm;
		
		this.top = fm.getMaxAscent()*1.3f;
		this.bottom = fm.getMaxDescent()*1.3f;
		this.ascent = fm.getAscent()*1.3f;
		this.descent = fm.getDescent()*1.3f;
	}
}
