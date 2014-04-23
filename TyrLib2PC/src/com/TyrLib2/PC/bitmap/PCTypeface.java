package com.TyrLib2.PC.bitmap;

import java.awt.Font;

import com.tyrlib2.bitmap.ITypeface;

public class PCTypeface implements ITypeface {
	public final Font font;
	
	public PCTypeface(Font font) {
		this.font = font;
	}
}
