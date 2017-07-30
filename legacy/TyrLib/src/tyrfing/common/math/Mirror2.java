package tyrfing.common.math;

public class Mirror2 implements Transformer {

	
	public static final Mirror2 MirrorY = new Mirror2(90);
	
	private Vector2 transformX;
	private Vector2 transformY;
	
	public Mirror2(float degree)
	{
		/***************
		 * cos 2a| sin 2a
		 * sin 2a| -cos 2a
		 */
		
		degree = (float)Math.toRadians(degree);
		
		transformX = new Vector2((float)Math.cos(degree*2), (float)Math.sin(degree*2));
		transformY = new Vector2((float)Math.sin(degree*2), -1 * (float)Math.cos(degree*2));
		transformX.x = ((int)transformX.x * 10000) / 10000.f;
		transformX.y = ((int)transformX.y * 10000) / 10000.f;
		transformY.x = ((int)transformY.x * 10000) / 10000.f;
		transformY.y = ((int)transformY.y * 10000) / 10000.f;
		
		
	
	}
	
	public Vector2 transformVector(Vector2 vector2)
	{
		
		Vector2 transformed = new Vector2(0,0);
		transformed.x = vector2.dot(transformX);
		transformed.y = vector2.dot(transformY);
		
		return transformed;
		
	}

}
