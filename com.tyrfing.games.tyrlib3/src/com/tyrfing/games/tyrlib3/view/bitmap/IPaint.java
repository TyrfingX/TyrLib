package com.tyrfing.games.tyrlib3.view.bitmap;

import com.tyrfing.games.tyrlib3.model.game.Color;


public interface IPaint {
	
	public enum TextAlign {
		LEFT,
		CENTER,
		RIGHT
	};
	
	public void setAntiAlias(boolean state);
	public void setTextSize(int size);
	public void setColor(int color);
	public void setColor(Color color);
	public void setTypeface(ITypeface typeface);
	public IFontMetrics getFontMetrics();
	public void getTextWidths(char[] s, int index, int count, float[] widths);
	public void setTextAlign(TextAlign align);
}
