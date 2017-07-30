package tyrfing.games.id3.lib;

import java.util.LinkedList;
import java.util.Queue;

import tyrfing.common.math.Vector2;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.struct.Node;
import tyrfing.games.id3.lib.rooms.Room;
import tyrfing.games.id3.lib.rooms.RoomFactory;

public class Preview {
	protected Node node;
	protected Queue<Room> queue;
	protected RoomFactory factory;
	protected final int ITEMS;
	protected final float space;
	
	public Preview(Node node, RoomFactory factory, int items)
	{
		this.node = node;
		this.queue = new LinkedList<Room>();
		this.factory = factory;
		this.ITEMS = items;
	
		space = 0.75f*TargetMetrics.width / items;
	
	}
	
	public void generate()
	{
		for (int i = 0; i < ITEMS; i++)
		{
			this.addRoom();
		}
	}
	
	public Room step()
	{
		Room room = queue.poll();
		
		for (Room waiting : queue)
		{
			waiting.translate(-space, 0);
		}
		
		this.addRoom();
		node.removeChild(room.getNode());
		factory.getRootNode().addChild(room.getNode());
		factory.positionRoom(room);
		return room;
	}
	
	private void addRoom()
	{
		Room room = factory.createRandomRoom();
		node.addChild(room.getNode());
		room.setPos(new Vector2(queue.size() * space+0.125f*TargetMetrics.width, 0));
		queue.add(room);		
	}
	
	public void addRoom(Room room)
	{
		room.getNode().getParent().removeChild(room.getNode());
		node.addChild(room.getNode());
		room.setPos(new Vector2(queue.size() * space+0.125f*TargetMetrics.width, 0));
		queue.add(room);
	}
	
	public void destroy()
	{
		for (Room waiting : queue)
		{
			waiting.destroy();
		}		
		
	}
}
