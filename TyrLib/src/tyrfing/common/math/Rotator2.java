package tyrfing.common.math;

public class Rotator2 implements Transformer {

	
	public static final Rotator2 Rotator90 = new Rotator2(90);
	
	private Vector2 transformX;
	private Vector2 transformY;
	
	public Rotator2(float degree)
	{
		/***************
		 * cos | -sin
		 * sin | cos
		 */
		
		degree = (float)Math.toRadians(degree);
		
		transformX = new Vector2((float)Math.cos(degree), -1 * (float)Math.sin(degree));
		transformY = new Vector2((float)Math.sin(degree), (float)Math.cos(degree));
		
		
	}
	
	public Vector2 transformVector(Vector2 vector2)
	{
		
		Vector2 transformed = new Vector2(0,0);
		transformed.x = vector2.dot(transformX);
		transformed.y = vector2.dot(transformY);
		
		return transformed;
		
	}
}
