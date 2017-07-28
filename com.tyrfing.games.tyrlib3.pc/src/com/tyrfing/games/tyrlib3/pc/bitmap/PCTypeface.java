package com.tyrfing.games.tyrlib3.pc.bitmap;

import java.awt.Font;

import com.tyrfing.games.tyrlib3.bitmap.ITypeface;

public class PCTypeface implements ITypeface {
	public final Font font;
	
	public PCTypeface(Font font) {
		this.font = font;
	}
}
