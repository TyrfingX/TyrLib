package com.tyrfing.games.id17.holdings;

import java.io.Serializable;

public class BaronyWindow implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5954618873789801129L;
	
	public short x, y, w, h;
	public long color;
	
	public BaronyWindow() {
		
	}
	
	public BaronyWindow(short x, short y, short w, short h, long color) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.color = color;
	}
}
