package com.tyrlib2.gui;

import com.tyrlib2.util.Color;

public class Paint {
	public Color color;
	public int borderWidth;
	public Color borderColor;
	
	public Paint(Color fill, Color border, int borderWidth) {
		this.color = fill;
		this.borderColor = border;
		this.borderWidth = borderWidth;
	}
}
