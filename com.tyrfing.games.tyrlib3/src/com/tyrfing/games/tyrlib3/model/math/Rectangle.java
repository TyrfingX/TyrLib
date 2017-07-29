package com.tyrfing.games.tyrlib3.model.math;

public class Rectangle {
	public static boolean pointInRectangle(Vector2F pos, Vector2F size, Vector2F point) {
		if (point.x >= pos.x && point.x <= pos.x + size.x) {
			if (point.y <= pos.y && point.y >= pos.y - size.y) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean overlap(Vector2F min1, Vector2F max1, Vector2F min2, Vector2F max2) {
	    if (min1.x > max2.x) return false;
	    if (min2.x > max1.x) return false;
	    if (min1.y > max2.y) return false;
	    if (min2.y > max1.y) return false;
	    
	    return true;
	}
	
	public static boolean overlap(float min1X, float min1Y, float max1X, float max1Y, float min2X, float min2Y, float max2X, float max2Y) {
	    if (min1X > max2X) return false;
	    if (min2X > max1X) return false;
	    if (min1Y > max2Y) return false;
	    if (min2Y > max1Y) return false;
	    
	    return true;
	}
}
