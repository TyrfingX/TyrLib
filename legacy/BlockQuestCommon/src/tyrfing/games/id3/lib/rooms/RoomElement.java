package tyrfing.games.id3.lib.rooms;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Color;
import android.os.Debug;
import tyrfing.common.game.objects.Direction;
import tyrfing.common.game.objects.GameObject;
import tyrfing.common.game.objects.Movement;
import tyrfing.common.math.Mirror2;
import tyrfing.common.math.Rotator2;
import tyrfing.common.math.Transformer;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;
import tyrfing.common.renderables.Image;
import tyrfing.common.renderables.Line;
import tyrfing.common.renderables.Rectangle;
import tyrfing.common.struct.Node;
import tyrfing.games.id3.lib.GlobalSettings;


public class RoomElement extends GameObject {
	
	protected Room room;
	protected Rectangle overlay;
	protected Image bg;
	protected Map<Direction, Door> doors;
	protected Map<Direction, Line> walls;
	protected float tileSize;
	protected boolean red;
	protected Movement movement;
	protected boolean justLanded;
	
	public RoomElement(Node node, float tileSize)
	{
		super(node);
		this.tileSize = tileSize;
		walls = new ConcurrentHashMap<Direction, Line>();
		doors = new ConcurrentHashMap<Direction, Door>();
		//if (!Debug.isDebuggerConnected())
		{
			bg = SceneManager.createImage(Ressources.getBitmap("roomBg"), node);
		}
		justLanded = true;
		movement = new Movement(node, Room.fallSpeed * Room.SPEED_UP);
	}
	
	public static boolean isDoorOpen(RoomElement element1, RoomElement element2, Direction direction)
	{
		if (element1.doors.containsKey(direction))
		{
			Door door1 = element1.doors.get(direction);
			if (door1.getType() == DoorType.GREEN)
			{
				Direction opposite = direction.turnRight().turnRight();
				return element2.hasDoor(opposite);
			}
			else
			{
				return true;
			}
			
		}
		else
		{
			Direction opposite = direction.turnRight().turnRight();
			if (element2.doors.containsKey(opposite))
			{
				Door door2 = element2.doors.get(opposite);
				return (door2.getType() != DoorType.GREEN);
			}
			else
			{
				return false;
			}
		}
		
	}
	
	public boolean addDoor(Door door)
	{
		if (walls.containsKey(door.getDirection()))
		{
			doors.put(door.getDirection(), door);
			if (door.getType() == DoorType.RED)
			{
				walls.get(door.getDirection()).getPaint().setColor(GlobalSettings.doorColor.toColor());
			}
			else
			{
				walls.get(door.getDirection()).getPaint().setColor(Color.GREEN);
			}
			walls.get(door.getDirection()).getPaint().setStrokeWidth(3);
			walls.get(door.getDirection()).setPriority(20);
			return true;
		}
		return false;
	}
	
	public boolean isOpen(Direction dir)
	{
		if (walls.containsKey(dir))
		{
			return !walls.get(dir).getVisible();
		}
		return true;
	}
	
	public boolean hasDoor(Direction dir)
	{
		return doors.containsKey(dir) || !walls.containsKey(dir);
	}
	
	public void openDoor(Direction dir)
	{
		if (doors.containsKey(dir))
		{
			Door door = doors.get(dir);
			door.openUp();
		}
		
		if (walls.containsKey(dir))
		{
			walls.get(dir).setVisible(false);
		}
	}
	
	public void closeDoor(Direction dir)
	{
		if (doors.containsKey(dir))
		{
			Door door = doors.get(dir);
			door.close();		
		}
		
		if (walls.containsKey(dir))
		{
			walls.get(dir).setVisible(true);
		}
		else
		{
			
			Line wall = null;
			
			switch (dir)
			{
			case RIGHT:
				wall = SceneManager.createLine(0, this.getHeight(), Color.GRAY, node.createChild(this.getWidth(), 0));
				this.addWall(wall, Direction.RIGHT);
				break;
			case LEFT:
				wall = SceneManager.createLine(0, this.getHeight(), Color.GRAY, node.createChild(0, 0));
				this.addWall(wall, Direction.LEFT);	
				break;
			case UP:
				wall = SceneManager.createLine(this.getWidth(), 0, Color.GRAY, node.createChild(0, 0));
				this.addWall(wall, Direction.UP);	
				break;
			case DOWN:
				wall = SceneManager.createLine(this.getWidth(), 0, Color.GRAY, node.createChild(0, this.getHeight()));
				this.addWall(wall, Direction.DOWN);
				break;
			}
			
			this.addWall(wall, dir);
			this.addDoor(new Door(dir, DoorType.RED));
		}
	}
	
	public void setRoom(Room room)
	{
		if (room != null)
		{
			if (room.isRed() && (this.room == null || !this.room.isRed()))
			{
				this.onRedSet(room.getState());
			}
		}
		this.room = room;
		
	}
	
	public void onRedSet(RoomState state)
	{
		if (bg != null)
		{
			SceneManager.RENDER_THREAD.removeRenderable(bg);
			if (state == RoomState.RED)
			{
				bg = SceneManager.createImage(Ressources.getBitmap("roomBgDarkRed"), node);
			}
			else if (state == RoomState.MADERED)
			{
				bg = SceneManager.createImage(Ressources.getBitmap("roomBgRed"), node);
			}
			else if (state == RoomState.VIOLET)
			{
				bg = SceneManager.createImage(Ressources.getBitmap("roomBgViolet"), node);
			}
			else if (state == RoomState.CURSED)
			{
				bg = SceneManager.createImage(Ressources.getBitmap("cursedBg"), node);
			}
			else if (state == RoomState.MADEBLUE)
			{
				bg = SceneManager.createImage(Ressources.getBitmap("roomBgBlue"), node);
			}
			else if (state == RoomState.BLUE)
			{
				bg = SceneManager.createImage(Ressources.getBitmap("roomBgDarkBlue"), node);
			}
		}
		
	}
	
	public void onRedRemoved()
	{
		if (bg != null)
		{
			//if (!Debug.isDebuggerConnected())
			{
				SceneManager.RENDER_THREAD.removeRenderable(bg);
				bg = SceneManager.createImage(Ressources.getBitmap("roomBg"), node);
			}
		}
	}
	
	public Room getRoom()
	{
		return room;
	}

	public void cleared()
	{
		overlay = SceneManager.createRectangle(getWidth(), getHeight(), Color.BLACK, node);
		overlay.getPaint().setAlpha(80);
		overlay.setPriority(5);
	}
	

	public void transform(Transformer transformer)
	{
		Vector2 relPos;
		Vector2 newRelPos;
		
		relPos = this.getPos();
		
		newRelPos = transformer.transformVector(relPos);
		
		this.setPos(newRelPos);

		Map<Direction, Line> tmp = new HashMap<Direction, Line>();
		tmp.putAll(walls);
		Set<Direction> wallsDir = tmp.keySet();
		walls.clear();
		
		for (Direction dir : wallsDir)
		{
			Line line = tmp.get(dir);
			Node child = line.getParent();
			relPos = child.getPos().sub(new Vector2(this.getWidth(), 0));
			newRelPos = transformer.transformVector(relPos);
			
			if (transformer instanceof Rotator2)
			{
				newRelPos = newRelPos.add(this.getSize());
			}
			
			child.setPos(newRelPos);

			
			relPos = line.getTo();
			newRelPos = transformer.transformVector(relPos);
			line.setTo(newRelPos);
			
			if (transformer instanceof Mirror2)
			{
				if (dir == Direction.LEFT || dir == Direction.RIGHT)
				{
					walls.put(dir.turnRight().turnRight(), line);
				}
				else
				{
					walls.put(dir, line);
				}
			}
			else
			{
				walls.put(dir.turnRight(), line);
			}
			
			
			
		}
		
		Map<Direction, Door> tmpDoors = new HashMap<Direction, Door>();
		tmpDoors.putAll(doors);
		Set<Direction> doorDirs = tmp.keySet();
		doors.clear();		
		
		for (Direction dir : doorDirs)
		{
			Door door = tmpDoors.get(dir);
			if (door != null)
			{
				if (transformer instanceof Mirror2)
				{
					if (dir == Direction.LEFT || dir == Direction.RIGHT)
					{
						doors.put(dir.turnRight().turnRight(), door);
					}
					else
					{
						doors.put(dir, door);
					}
				}
				else
				{
					doors.put(dir.turnRight(), door);
				}
			}
		}
	}
	
	@Override
	public void onUpdate(float time) {
		while (time > 0)
		{
			if (!movement.isFinished())
			{
				movement.onUpdate(time);
				time = movement.getRemainingTime();
			}
			else
			{
				time = 0;
			}
		}
	}

	@Override
	public float getWidth() {
		return tileSize;
	}

	@Override
	public float getHeight() {
		return tileSize;
	}
	
	public Vector2 getCenter()
	{
		Vector2 pos = super.getCenter();
		Node parent = node.getParent();
		if (parent.getParent() != null) parent = parent.getParent();
		pos = pos.sub(parent.getAbsolutePos());
		return pos;
	}
	
	public void destroy()
	{
		if (overlay != null) SceneManager.RENDER_THREAD.removeRenderable(overlay);
		for (Line line : walls.values())
		{
			SceneManager.RENDER_THREAD.removeRenderable(line);
		}
		SceneManager.RENDER_THREAD.removeRenderable(bg);
		
		walls.clear();
		doors.clear();
		
	}
	
	public void finishConstruction()
	{

		if (this.isEmpty(new Vector2(this.getSize().x,0)) && !walls.containsKey(Direction.RIGHT))
		{
			Line wall = SceneManager.createLine(0, this.getHeight(), Color.BLACK, node.createChild(this.getWidth(), 0));
			this.addWall(wall, Direction.RIGHT);
		}
		
		if (this.isEmpty(new Vector2(-this.getSize().x,0)) && !walls.containsKey(Direction.LEFT))
		{
			Line wall = SceneManager.createLine(0, this.getHeight(), Color.BLACK, node.createChild(0, 0));
			this.addWall(wall, Direction.LEFT);		
		}
		
		if (this.isEmpty(new Vector2(0,this.getSize().y)) && !walls.containsKey(Direction.DOWN))
		{
			Line wall = SceneManager.createLine(this.getWidth(), 0, Color.BLACK, node.createChild(0, this.getHeight()));
			this.addWall(wall, Direction.DOWN);
		}
		
		if (this.isEmpty(new Vector2(0,-this.getSize().y)) && !walls.containsKey(Direction.UP))
		{
			Line wall = SceneManager.createLine(this.getWidth(), 0, Color.BLACK, node.createChild(0, 0));
			this.addWall(wall, Direction.UP);
		}
		
	}
	
	
	public boolean isEmpty(Vector2 offset)
	{
		return (this.getNeighbour(offset) == null);
	}
	
	public RoomElement getNeighbour(Vector2 offset)
	{
		for (int i = 0; i < room.getCountElements(); i++)
		{
			RoomElement other = room.getRoomElement(i);
			if (other != this)
			{
				Vector2 oPos = this.getCenter().add(offset);
				Vector2 otherPos = other.getCenter().sub(other.getSize().multiply(0.5f));
				if (oPos.x >= otherPos.x && oPos.x <= otherPos.add(other.getSize()).x)
				{
					if (oPos.y >= otherPos.y && oPos.y <= otherPos.add(other.getSize()).y)
					{
						return other;
					}	
				}
			}
		}		
		return null;
	}
	
	public void addWall(Line wall, Direction dir)
	{
		walls.put(dir, wall);
		
		if (this.hasDoor(dir))
		{
			wall.getPaint().setColor(Color.RED);
			wall.setPriority(20);
			wall.getPaint().setStrokeWidth(2);
		}
		else
		{
			wall.getPaint().setStrokeWidth(4);
			wall.setPriority(1);
		}

	}


}
