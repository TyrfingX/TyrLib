package tyrfing.games.BlockQuest.rooms;

import java.util.ArrayList;

import tyrfing.common.math.Vector2;
import tyrfing.common.struct.Coord2;
import tyrfing.games.BlockQuest.lib.MainLogic;

public class RoomShape {
	private ArrayList<Vector2> elements;
	private float tileSize;

	public RoomShape()
	{
		elements = new ArrayList<Vector2>();
		tileSize = MainLogic.calcTileSize();
	}
	
	public void addElement(Coord2 pos)
	{
		elements.add(new Vector2(pos.x * tileSize, pos.y * tileSize));
	}
	
	public int countElements()
	{
		return elements.size();
	}
	
	public Vector2 getElement(int index)
	{
		return elements.get(index);
	}
}
