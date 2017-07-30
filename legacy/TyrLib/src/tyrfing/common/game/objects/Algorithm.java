package tyrfing.common.game.objects;

import java.util.Collection;

import tyrfing.common.math.Vector2;

public class Algorithm {
	public static Vector2 getMin(Collection<? extends GameObject> objects)
	{
		Vector2 minV = null;
		
		for (GameObject object : objects)
		{
			Vector2 pos = object.getAbsolutePos();
			if (minV == null)
			{
				minV = pos;
			}
			else
			{
				if (minV.x > pos.x)
				{
					minV.x = pos.x;
				}
				
				if (minV.y > pos.y)
				{
					minV.y = pos.y;
				}
			}
		}		
		return minV;
	}
	
	public static Vector2 getMax(Collection<? extends GameObject> objects)
	{
		Vector2 maxV = null;
		
		for (GameObject object : objects)
		{
			Vector2 pos = object.getAbsolutePos().add(object.getSize());
			if (maxV == null)
			{
				maxV = pos;
			}
			else
			{
				if (maxV.x < pos.x)
				{
					maxV.x = pos.x;
				}
				
				if (maxV.y < pos.y)
				{
					maxV.y = pos.y;
				}
			}
		}		
		return maxV;
	}
	
	public static boolean testPointInRect(Vector2 min, Vector2 max, Vector2 point)
	{
		if (point.x >= min.x && point.y >= min.y)
		{
			if (point.x <= max.x && point.y <= max.y)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean testRectIntersect(Vector2 min1, Vector2 max1, Vector2 min2, Vector2 max2) {
		if (max1.x < min2.x) return false;
		if (min1.x > max2.x) return false;
		if (max1.y < min2.y) return false;
		if (min1.y > max2.y) return false;
		
		return true;
	}

	public static boolean testPointInCircle(Vector2 center, float radius, Vector2 point)
	{	
		return (center.x - point.x)*(center.x - point.x) + (center.y - point.y)*(center.y - point.y) <= radius*radius;
	}
	
}
