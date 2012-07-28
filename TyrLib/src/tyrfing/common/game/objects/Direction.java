package tyrfing.common.game.objects;

import tyrfing.common.math.Vector2;
import tyrfing.common.struct.Coord2;

public enum Direction {
	LEFT,
	RIGHT,
	DOWN,
	UP;
	
	public static Direction getOppositeDirection(Direction direction)
	{
		return direction.turnRight().turnRight();
	}
	
	public static Direction getRandomDirection()
	{
		Direction[] values = Direction.values();
		Direction dir = values[(int)(Math.random() * values.length)];
		return dir;
	}
	
	
	public Vector2 translatePoint(Vector2 point, float translation) {
		switch (this)
		{
		case LEFT:
			point = point.add(new Vector2(-translation, 0));
			break;
		case RIGHT:
			point = point.add(new Vector2(translation, 0));
			break;
		case DOWN:
			point = point.add(new Vector2(0, translation));
			break;
		case UP:
			point = point.add(new Vector2(0, -translation));
			break;
		}
		return point;
	}
	
	
	public static Vector2 translatePoint(Direction dir, Vector2 point, float translation)
	{
		switch (dir)
		{
		case LEFT:
			point = point.add(new Vector2(-translation, 0));
			break;
		case RIGHT:
			point = point.add(new Vector2(translation, 0));
			break;
		case DOWN:
			point = point.add(new Vector2(0, translation));
			break;
		case UP:
			point = point.add(new Vector2(0, -translation));
			break;
		}
		return point;
	}
	
	public static Coord2 translatePoint(Direction dir, Coord2 point, int translation)
	{
		switch (dir)
		{
		case LEFT:
			point = new Coord2(point.x - translation, point.y);
			break;
		case RIGHT:
			point = new Coord2(point.x + translation, point.y);
			break;
		case DOWN:
			point = new Coord2(point.x, point.y + translation);
			break;
		case UP:
			point = new Coord2(point.x, point.y - translation);
			break;
		}
		return point;
	}
	
	public Direction turnRight()
	{
		switch (this)
		{
		case LEFT:
			return UP;
		case RIGHT:
			return DOWN;
		case DOWN:
			return LEFT;
		case UP:
			return RIGHT;
		}		
		
		return null;
	}
	
}
