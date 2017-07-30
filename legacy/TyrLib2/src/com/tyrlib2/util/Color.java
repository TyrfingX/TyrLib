package com.tyrlib2.util;

import java.io.Serializable;
import java.util.Random;

public class Color implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6096186688785078790L;
	
	public static final Color BLACK = new Color(0,0,0,1);
	public static final Color GRAY = new Color(0.3f,0.3f,0.3f,1);
	public static final Color DARK_GRAY = new Color(0.15f,0.15f,0.15f,1);
	public static final Color WHITE = new Color(1,1,1,1);
	public static final Color RED = new Color(1,0,0,1);
	public static final Color ORANGE = new Color(1,0.5f,0.5f,1);
	public static final Color GREEN = new Color(0,1,0,1);
	public static final Color BLUE = new Color(0,0,1,1);
	public static final Color YELLOW = new Color(1,1,0,1);
	public static final Color TRANSPARENT = new Color(1,1,1,0);
	
	public float r,g,b,a;
	
	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	public static Color fromRGBA(int r, int g, int b, int a) {
		return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
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
	
	public static Color getRandomColor(float min) {
		Random random = new Random();
		return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat() + min);
	}
	
	public static Color lerp(Color c1, Color c2, float weight) {
		return new Color(	c1.r * weight + c2.r * (1-weight),
							c1.g * weight + c2.g * (1-weight),
							c1.b * weight + c2.b * (1-weight),
							c1.a * weight + c2.a * (1-weight));
	}
	
	public String toHex() {
		return String.format("#%02x%02x%02x", (int)(r*255), (int)(g*255), (int)(b*255));
	}
}
