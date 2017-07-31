package com.tyrfing.games.tyrlib3.model.math;

public class Mirror2I implements Transformer2I {

	public static final Mirror2I MirrorY = new Mirror2I(90);
	
	private Vector2F transformX;
	private Vector2F transformY;
	
	public Mirror2I(float degree) {
		/***************
		 * cos 2a| sin 2a
		 * sin 2a| -cos 2a
		 */
		
		degree = (float)Math.toRadians(degree);
		
		transformX = new Vector2F((float)Math.cos(degree*2), (float)Math.sin(degree*2));
		transformY = new Vector2F((float)Math.sin(degree*2), -1 * (float)Math.cos(degree*2));
		transformX.x = ((int)transformX.x * 10000) / 10000.f;
		transformX.y = ((int)transformX.y * 10000) / 10000.f;
		transformY.x = ((int)transformY.x * 10000) / 10000.f;
		transformY.y = ((int)transformY.y * 10000) / 10000.f;
	}

	@Override
	public Vector2I transform(Vector2I vector2) {
		Vector2I transformed = new Vector2I();
		transformed.x = (int) vector2.dot(transformX);
		transformed.y = (int) vector2.dot(transformY);
		
		return transformed;
	}

}
