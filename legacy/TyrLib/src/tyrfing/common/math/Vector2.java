package tyrfing.common.math;

public class Vector2 {
	public float x;
	public float y;
	
	public Vector2(float x, float y) { this.x = x; this.y = y; }
	
	public Vector2 vectorTo(Vector2 other)
	{
		return new Vector2(other.x - this.x, other.y - this.y);
	}

	public float length()
	{
		return (float) Math.sqrt(this.x * this.x + this.y * this.y);
	}
	
	public float normalize()
	{
		float length = this.length();
		if (length != 0)
		{
			x /= length;
			y /= length;
		}
		return length;
	}
	
	public Vector2 multiply(float m)
	{
		return new Vector2(x*m, y*m);
	}
	
	public float magnitude()
	{
		return this.y / this.x;
	}
	
	public Vector2 add(Vector2 other)
	{
		return new Vector2(other.x + this.x, other.y + this.y);
	}
	
	public Vector2 sub(Vector2 other)
	{
		return new Vector2(this.x - other.x, this.y - other.y);
	}
	
	public float dot(Vector2 other)
	{
		return other.x * this.x + other.y * this.y;
	}
	
}
