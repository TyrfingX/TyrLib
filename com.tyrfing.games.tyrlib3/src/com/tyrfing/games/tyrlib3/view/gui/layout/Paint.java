package com.tyrfing.games.tyrlib3.view.gui.layout;

import com.tyrfing.games.tyrlib3.model.game.Color;

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
