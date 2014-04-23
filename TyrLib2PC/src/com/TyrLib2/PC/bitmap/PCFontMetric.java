package com.TyrLib2.PC.bitmap;

import java.awt.FontMetrics;

import com.tyrlib2.bitmap.IFontMetrics;


public class PCFontMetric extends IFontMetrics {
	public final FontMetrics fm;
	
	public PCFontMetric(FontMetrics fm) {
		this.fm = fm;
		
		this.top = fm.getMaxAscent();
		this.bottom = fm.getMaxDescent();
		this.ascent = fm.getAscent();
		this.descent = fm.getDescent();
	}
}
