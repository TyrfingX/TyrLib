package tyrfing.games.id3.lib.rooms;

import java.util.ArrayList;
import java.util.List;

import tyrfing.common.struct.Coord2;

public class RoomFactoryConfig {
	public boolean allowRedRooms;
	public boolean allowAttackUp = false;
	public boolean allowVioletRooms = false;
	public boolean allowCursedRooms = false;
	public boolean allowGreenDoors = false;
	public boolean allowBlueRooms = false;
	
	public int STAIRS_UP_MIN_ROOMS = 15;
	public float PROB_STAIRS_UP = 0.075f;
	public int STAIRS_DOWN_MIN_ROOMS = 40;
	public float PROB_STAIRS_DOWN = 0.2f;
	
	public float PROB_GREEN_DOOR = 0.1f;
	
	public float PROB_BLUE_ROOM = 0.05f;
	
	public  float PROB_PER_TILE = 0.13f;
	
	public float PROB_ORC_BOSS = 0;
	public float PROB_UNDEAD_BOSS = 0;
	
	public int MIN_DOORS = 1;
	public int MAX_DOORS = 4;
	
	public List<RoomShape> shapes;
	
	public void createDefaultShapes()
	{
		
		shapes = new ArrayList<RoomShape>();
		
		//L-Shape 1
		RoomShape shape = new RoomShape();
		shape.addElement(new Coord2(1,0));
		shape.addElement(new Coord2(-1,1));
		shape.addElement(new Coord2(0,1));
		shape.addElement(new Coord2(1,1));
		shapes.add(shape);
		
		//L-Shape 2
		shape = new RoomShape();
		shape.addElement(new Coord2(1,0));
		shape.addElement(new Coord2(1,-1));
		shape.addElement(new Coord2(0,1));
		shape.addElement(new Coord2(1,1));
		shapes.add(shape);
		
		//Straight line
		shape = new RoomShape();
		shape.addElement(new Coord2(-1,0));
		shape.addElement(new Coord2(0,0));
		shape.addElement(new Coord2(1,0));
		shapes.add(shape);

		//Z-Shape1
		shape = new RoomShape();
		shape.addElement(new Coord2(-1,0));
		shape.addElement(new Coord2(0,0));
		shape.addElement(new Coord2(0,1));
		shape.addElement(new Coord2(1,1));
		shapes.add(shape);
		
		//Z-Shape2
		shape = new RoomShape();
		shape.addElement(new Coord2(1,0));
		shape.addElement(new Coord2(0,0));
		shape.addElement(new Coord2(0,1));
		shape.addElement(new Coord2(-1,1));
		shapes.add(shape);

		//Bigger room
		shape = new RoomShape();
		shape.addElement(new Coord2(-1,0));
		shape.addElement(new Coord2(0,0));
		shape.addElement(new Coord2(-1,1));
		shape.addElement(new Coord2(0,1));
		shape.addElement(new Coord2(1,1));
		shapes.add(shape);
	}
}
