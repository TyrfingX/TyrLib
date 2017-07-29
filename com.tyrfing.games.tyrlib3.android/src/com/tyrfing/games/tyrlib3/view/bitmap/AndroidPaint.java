package com.tyrfing.games.tyrlib3.view.bitmap;

import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.view.bitmap.IFontMetrics;
import com.tyrfing.games.tyrlib3.view.bitmap.IPaint;
import com.tyrfing.games.tyrlib3.view.bitmap.ITypeface;

import android.graphics.Paint;
import android.graphics.Paint.Align;


public class AndroidPaint implements IPaint {

	public final Paint paint = new Paint();
	
	@Override
	public void setAntiAlias(boolean state) {
		paint.setAntiAlias(state);
	}

	@Override
	public void setTextSize(int size) {
		paint.setTextSize(size);
	}

	@Override
	public void setColor(int color) {
		paint.setColor(color);
	}

	@Override
	public void setTypeface(ITypeface typeface) {
		paint.setTypeface(((AndroidTypeface)typeface).tf);
	}

	@Override
	public IFontMetrics getFontMetrics() {
		return new AndroidFontMetrics(paint.getFontMetrics());
	}

	@Override
	public void getTextWidths(char[] s, int index, int count, float[] widths) {
		paint.getTextWidths(s, index, count, widths);
	}

	@Override
	public void setTextAlign(TextAlign align) {
		switch (align) {
		case LEFT:
			paint.setTextAlign(Align.LEFT);
			break;
		case CENTER:
			paint.setTextAlign(Align.CENTER);
			break;
		case RIGHT:
			paint.setTextAlign(Align.RIGHT);
			break;
		}
	}

	@Override
	public void setColor(Color color) {
		setColor(android.graphics.Color.argb(	(int)(color.a*255), 
												(int)(color.r*255), 
												(int)(color.g*255), 
												(int)(color.b*255)));
	}

}
