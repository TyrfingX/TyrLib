package tyrfing.common.struct;

import tyrfing.common.math.Vector2;

public class Coord2 {
	public int x;
	public int y;
	
	public Coord2(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Coord2(Vector2 vector2)
	{
		this.x = (int) vector2.x;
		this.y = (int) vector2.y;
	}
}
