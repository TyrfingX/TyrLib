package com.tyrfing.games.tyrlib3.pc.view.bitmap;

import java.awt.FontMetrics;

import com.tyrfing.games.tyrlib3.view.bitmap.IFontMetrics;


public class PCFontMetric extends IFontMetrics {
	public PCFontMetric(FontMetrics fm) {
		this.top = fm.getMaxAscent();
		this.bottom = (fm.getMaxDescent()*2f);
		this.ascent = fm.getAscent();
		this.descent = fm.getDescent();
	}
}
