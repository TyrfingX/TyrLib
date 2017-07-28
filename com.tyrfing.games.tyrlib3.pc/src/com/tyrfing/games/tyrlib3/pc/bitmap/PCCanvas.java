package com.tyrfing.games.tyrlib3.pc.bitmap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.tyrfing.games.tyrlib3.bitmap.ICanvas;
import com.tyrfing.games.tyrlib3.bitmap.IDrawableBitmap;
import com.tyrfing.games.tyrlib3.bitmap.IPaint;
import com.tyrfing.games.tyrlib3.bitmap.IPaint.TextAlign;
import com.tyrfing.games.tyrlib3.math.Rect;

public class PCCanvas implements ICanvas {
	
	public BufferedImage canvas;
	
	public PCCanvas(BufferedImage canvas) {
		this.canvas = canvas;
	}
	
	@Override
	public void drawText(char[] text, int index, int count, float x, float y, IPaint paint) {
		String s = new String(text, index, count);
		drawText(s, x, y, paint);
	}
	
	@Override
	public void drawText(String s, float x, float y, IPaint paint) {
		PCPaint p = (PCPaint) paint;
		Font font = p.g.getFont();
		Graphics2D g = (Graphics2D) canvas.getGraphics().create();
		
		g.setFont(font);
		g.setRenderingHint(	RenderingHints.KEY_ANTIALIASING, // Anti-alias!
		        			RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setRenderingHint(	RenderingHints.KEY_RENDERING,
							RenderingHints.VALUE_RENDER_QUALITY);
		
		g.setColor(p.g.getColor());
		
		if (p.align == TextAlign.RIGHT) {
			int textWidth = g.getFontMetrics().stringWidth(s);
	        x = x - textWidth;
		} 
		
		g.drawString(s, (int) x, (int) y);
		g.dispose();
	}

	@Override
	public void setBitmap(IDrawableBitmap bitmap) {
		PCDrawableBitmap b = (PCDrawableBitmap) bitmap;
		canvas = b.canvas;
	}

	@Override
	public void drawArc(Rect rect, float startDegree, float degree, boolean useCenter, IPaint paint) {
		
		PCPaint p = (PCPaint) paint;
		
		Graphics2D g = (Graphics2D) canvas.getGraphics().create();
		g.setColor(p.g.getColor());
		g.fillArc((int)rect.left, (int)rect.top, (int)(rect.right-rect.left), (int)(rect.bottom-rect.top), (int)-startDegree, (int)-degree);
		g.dispose();
	}

	@Override
	public void setRGB(int x, int y, com.tyrfing.games.tyrlib3.util.Color color) {
		canvas.setRGB(x, y, new Color(	(int)(color.r*255), 
										(int)(color.g*255), 
										(int)(color.b*255),
										(int)(color.a*255)).getRGB());
	}
}
