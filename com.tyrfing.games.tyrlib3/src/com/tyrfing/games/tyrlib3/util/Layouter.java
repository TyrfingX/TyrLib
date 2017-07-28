package com.tyrfing.games.tyrlib3.util;

import com.tyrfing.games.tyrlib3.main.Media;
import com.tyrfing.games.tyrlib3.math.Vector2F;

public class Layouter {
	public static Vector2F absoluteToRelative(Vector2F absolute) {
		Vector2F screenSize = Media.CONTEXT.getScreenSize();
		Vector2F relative = new Vector2F(absolute.x / screenSize.x, absolute.y / screenSize.y);
		return relative;
	}
	
	public static Vector2F restrict(Vector2F size, Vector2F max) {
		Vector2F screenSize = Media.CONTEXT.getScreenSize();
		Vector2F restricted = new Vector2F(size);
		if (max.x > 0 && screenSize.x * size.x > max.x ) restricted.x = max.x / screenSize.x;
		if (max.y > 0 && screenSize.y * size.y > max.y ) restricted.y = max.y / screenSize.y;
		return restricted;
	}
	
	public static float restrictX(float value, float max) {
		Vector2F screenSize = Media.CONTEXT.getScreenSize();
		if (screenSize.x * value > max ) value = max / screenSize.x;
		return value;
	}
	
	public static float restrictY(float value, float max) {
		Vector2F screenSize = Media.CONTEXT.getScreenSize();
		if (screenSize.y * value > max ) value = max / screenSize.y;
		return value;
	}
	
	public static Vector2F fitRatio(float width) {
		Vector2F screenSize = Media.CONTEXT.getScreenSize();
		float ratio = screenSize.x / screenSize.y;
		float height = width * ratio;
		return new Vector2F(width, height);
	}
}
