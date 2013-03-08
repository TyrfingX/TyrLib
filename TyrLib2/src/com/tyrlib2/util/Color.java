package com.tyrlib2.util;

import java.util.Random;

public class Color {
	
	public static final Color BLACK = new Color(0,0,0,1);
	public static final Color WHITE = new Color(1,1,1,1);
	public static final Color RED = new Color(1,0,0,1);
	public static final Color GREEN = new Color(0,1,0,1);
	public static final Color BLUE = new Color(0,0,1,1);
	public static final Color TRANSPARENT = new Color(1,1,1,0);
	
	public float r,g,b,a;
	
	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public void clamp() {
		if (r < 0) {
			r = 0;
		} else if (r > 1) {
			r = 1;
		}
		
		if (g < 0) {
			g = 0;
		} else if (g > 1) {
			g = 1;
		}
		
		if (b < 0) {
			b = 0;
		} else if (b > 1) {
			b = 1;
		}
		
		if (a < 0) {
			a = 0;
		} else if (a > 1) {
			a = 1;
		}
	}
	
	public Color copy() {
		return new Color(r,g,b,a);
	}
	
	public static Color getRandomColor() {
		Random random = new Random();
		return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat());
	}
}
