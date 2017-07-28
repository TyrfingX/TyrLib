package com.TyrLib2.PC.bitmap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.tyrlib2.bitmap.IFontMetrics;
import com.tyrlib2.bitmap.IPaint;
import com.tyrlib2.bitmap.ITypeface;

public class PCPaint implements IPaint {
	
	public final Graphics g;
	public final PCCanvas canvas;
	
	public TextAlign align = TextAlign.LEFT;
	
	public PCPaint(Graphics g, PCCanvas canvas) {
		this.g = g.create();
		this.canvas = canvas;
		Font font = g.getFont().deriveFont(Font.BOLD);
		g.setFont(font);
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
			Font font =  g.getFont();
			Graphics2D g = (Graphics2D) canvas.canvas.getGraphics().create();
			
			g.setFont(font);
			g.setRenderingHint(	RenderingHints.KEY_ANTIALIASING, // Anti-alias!
			        			RenderingHints.VALUE_ANTIALIAS_ON);
			
			g.setRenderingHint(	RenderingHints.KEY_RENDERING,
								RenderingHints.VALUE_RENDER_QUALITY);
			
			g.setColor(g.getColor());
			
			widths[i] = g.getFontMetrics().stringWidth(new String(s, i, 1));
			g.dispose();
		
		}
	}

	@Override
	public void setTextAlign(TextAlign align) {
		this.align = align;
	}
}
