package tyrfing.games.BlockQuest.rooms;


import tyrfing.common.game.objects.Direction;

public class Door{
	
	protected Direction dir;
	protected boolean open;
	protected DoorType type;
	
	public Door(Direction dir, DoorType type)
	{
		this.dir = dir;
		this.open = false;
		this.type = type;
	}
	
	public Direction getDirection()
	{
		return dir;
	}
	
	public void openUp()
	{
		this.open = true;
	}
	
	public void close()
	{
		this.open = false;
	}
	
	public boolean isOpen()
	{
		return open;
	}
	
	public DoorType getType()
	{
		return type;
	}
	
	public void setDoorType(DoorType type)
	{
		this.type = type;
	}
	
}
