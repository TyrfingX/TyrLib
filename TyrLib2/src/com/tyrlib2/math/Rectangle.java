package com.tyrlib2.math;

public class Rectangle {
	public static boolean pointInRectangle(Vector2 pos, Vector2 size, Vector2 point) {
		if (point.x >= pos.x && point.x <= pos.x + size.x) {
			if (point.y >= pos.y && point.y <= pos.y + size.y) {
				return true;
			}
		}
		
		return false;
	}
}
