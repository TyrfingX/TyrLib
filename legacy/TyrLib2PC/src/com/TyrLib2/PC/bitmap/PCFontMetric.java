package com.TyrLib2.PC.bitmap;

import java.awt.FontMetrics;

import com.tyrlib2.bitmap.IFontMetrics;


public class PCFontMetric extends IFontMetrics {
	public PCFontMetric(FontMetrics fm) {
		this.top = fm.getMaxAscent();
		this.bottom = (fm.getMaxDescent()*2f);
		this.ascent = fm.getAscent();
		this.descent = fm.getDescent();
	}
}
