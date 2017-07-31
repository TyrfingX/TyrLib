package com.tyrfing.games.tyrlib3.model.math;

public class Rotator2I implements Transformer2I {
	public static final Rotator2I Rotator90 = new Rotator2I(90);
	
	private Vector2F transformX;
	private Vector2F transformY;
	
	public Rotator2I(float degree) {
		/***************
		 * cos | -sin
		 * sin | cos
		 */
		
		degree = (float)Math.toRadians(degree);
		
		transformX = new Vector2F((float)Math.cos(degree), -1 * (float)Math.sin(degree));
		transformY = new Vector2F((float)Math.sin(degree), (float)Math.cos(degree));
	}

	@Override
	public Vector2I transform(Vector2I vector2) {
		Vector2I transformed = new Vector2I();
		transformed.x = (int) vector2.dot(transformX);
		transformed.y = (int) vector2.dot(transformY);
		
		return transformed;
	}
}
