package com.tyrlib2.util;

public class Color {
	
	public static final Color BLACK = new Color(0,0,0,1);
	public static final Color WHITE = new Color(1,1,1,1);
	public static final Color RED = new Color(1,0,0,1);
	public static final Color GREEN = new Color(0,1,0,1);
	public static final Color BLUE = new Color(0,0,1,1);
	
	public float r,g,b,a;
	
	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
}
