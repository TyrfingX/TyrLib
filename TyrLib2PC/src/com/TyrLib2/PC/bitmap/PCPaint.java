package com.TyrLib2.PC.bitmap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.tyrlib2.bitmap.IFontMetrics;
import com.tyrlib2.bitmap.IPaint;
import com.tyrlib2.bitmap.ITypeface;

public class PCPaint implements IPaint {
	
	public final Graphics g;
	
	public TextAlign align = TextAlign.LEFT;
	
	public PCPaint(Graphics g) {
		this.g = g.create();
	}
	
	@Override
	public void setAntiAlias(boolean state) {

	}

	@Override
	public void setTextSize(int size) {
		Font font = g.getFont().deriveFont((float)size);
		g.setFont(font);
	}

	@Override
	public void setColor(int color) {
		g.setColor(new Color(color));
	}
	
	@Override
	public void setColor(com.tyrlib2.util.Color color) {
		g.setColor(new Color(	(int)(color.r*255), 
								(int)(color.g*255), 
								(int)(color.b*255), 
								(int)(color.a*255)));
	}


	@Override
	public void setTypeface(ITypeface typeface) {
		g.setFont(((PCTypeface)typeface).font);
	}

	@Override
	public IFontMetrics getFontMetrics() {
		return new PCFontMetric(g.getFontMetrics());
	}

	@Override
	public void getTextWidths(char[] s, int index, int count, float[] widths) {
		for (int i = index; i < count; ++i) {
			widths[i] = (float) g.getFontMetrics().getStringBounds(new String(s), g).getWidth()/1.5f;
		}
	}

	@Override
	public void setTextAlign(TextAlign align) {
		this.align = align;
	}
}
