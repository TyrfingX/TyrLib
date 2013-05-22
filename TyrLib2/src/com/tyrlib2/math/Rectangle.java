package com.tyrlib2.math;

public class Rectangle {
	public static boolean pointInRectangle(Vector2 pos, Vector2 size, Vector2 point) {
		if (point.x >= pos.x && point.x <= pos.x + size.x) {
			if (point.y <= pos.y && point.y >= pos.y - size.y) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean overlap(Vector2 min1, Vector2 max1, Vector2 min2, Vector2 max2) {
	    if (min1.x > max2.x) return false;
	    if (min2.x > max1.x) return false;
	    if (min1.y > max2.y) return false;
	    if (min2.y > max1.y) return false;
	    
	    return true;
	}
}
