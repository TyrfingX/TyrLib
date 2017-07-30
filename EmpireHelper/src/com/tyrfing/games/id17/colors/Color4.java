package com.tyrfing.games.id17.colors;

public class Color4 implements Comparable<Color4> {
	public int r,g,b,a;
	
	public Color4(int r, int g, int b, int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color4() {
	}

	@Override
	public int compareTo(Color4 other) {
		if (g < other.g) {
			return -1;
		} else if (g > other.g) {
			return 1;
		}
		
		return 0;
	}
}
