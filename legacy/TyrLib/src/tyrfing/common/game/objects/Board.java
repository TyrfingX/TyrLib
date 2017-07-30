package tyrfing.common.game.objects;

import java.util.Collection;

import tyrfing.common.math.Vector2;
import tyrfing.common.struct.Coord2;
import tyrfing.common.struct.Grid;

public class Board extends Grid<GameObject>{
	
	protected float tileSize;
	
	public Board(int width, int height, float tileSize) {
		super(width, height);
		this.tileSize = tileSize;
	}
	
	public GameObject getItem(Vector2 point)
	{
		Coord2 coord = this.getBoardCoord(point);
		return this.getItem(coord);
	}
	
	public void setItem(Vector2 point, GameObject object)
	{
		Coord2 coord = this.getBoardCoord(point);
		this.setItem(coord, object);
	}
	
	public float getTileSize()
	{
		return tileSize;
	}
	
	public boolean pointInBoard(Vector2 point)
	{
		if (point.x < 0 || point.y < 0) return false;
		Coord2 boardCoord = this.getBoardCoord(point);
		return this.checkBounds(boardCoord.x, boardCoord.y);
	}
	
	public boolean itemsInBoard(Collection<? extends GameObject> items)
	{
		for (GameObject object : items)
		{
			Vector2 point = object.getCenter();
			if (!this.pointInBoard(point)) return false;
		}
		return true;
	}
	
	public Coord2 getBoardCoord(Vector2 point)
	{
		point = new Vector2(point.x, point.y);
		point.x /= tileSize;
		point.y /= tileSize;
		Coord2 boardCoord = new Coord2(point);
		
		return boardCoord;
	}
	
	public Vector2 toNearestBoardPoint(Vector2 point)
	{
		point = point.multiply(1.f/tileSize);		
		point.x = (int) point.x;
		point.y = (int) point.y;
		point = point.multiply(tileSize);
		return point;
	}

	
}
