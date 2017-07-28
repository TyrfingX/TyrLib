package com.tyrlib2.util;

import com.tyrlib2.main.Media;
import com.tyrlib2.math.Vector2;

public class Layouter {
	public static Vector2 absoluteToRelative(Vector2 absolute) {
		Vector2 screenSize = Media.CONTEXT.getScreenSize();
		Vector2 relative = new Vector2(absolute.x / screenSize.x, absolute.y / screenSize.y);
		return relative;
	}
	
	public static Vector2 restrict(Vector2 size, Vector2 max) {
		Vector2 screenSize = Media.CONTEXT.getScreenSize();
		Vector2 restricted = new Vector2(size);
		if (max.x > 0 && screenSize.x * size.x > max.x ) restricted.x = max.x / screenSize.x;
		if (max.y > 0 && screenSize.y * size.y > max.y ) restricted.y = max.y / screenSize.y;
		return restricted;
	}
	
	public static float restrictX(float value, float max) {
		Vector2 screenSize = Media.CONTEXT.getScreenSize();
		if (screenSize.x * value > max ) value = max / screenSize.x;
		return value;
	}
	
	public static float restrictY(float value, float max) {
		Vector2 screenSize = Media.CONTEXT.getScreenSize();
		if (screenSize.y * value > max ) value = max / screenSize.y;
		return value;
	}
	
	public static Vector2 fitRatio(float width) {
		Vector2 screenSize = Media.CONTEXT.getScreenSize();
		float ratio = screenSize.x / screenSize.y;
		float height = width * ratio;
		return new Vector2(width, height);
	}
}
