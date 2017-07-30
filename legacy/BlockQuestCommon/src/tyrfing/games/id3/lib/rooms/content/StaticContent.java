package tyrfing.games.id3.lib.rooms.content;

import tyrfing.common.game.objects.GameObject;
import tyrfing.common.render.SceneManager;
import tyrfing.common.renderables.Image;
import tyrfing.common.struct.Node;
import tyrfing.games.id3.lib.rooms.RoomElement;

public class StaticContent extends GameObject {

	public StaticContent() {
		super(new Node(0,0));
	}

	@Override
	public void onUpdate(float time) {}

	
	public void assignRoom(RoomElement element)
	{
		this.element = element;
		this.element.getNode().addChild(node);
		this.element.getRoom().enter(this);
	}
	
	public void remove()
	{
		SceneManager.RENDER_THREAD.removeRenderable(entity);
		element.getRoom().leave(this);
	}
	
	public RoomElement getRoomElement()
	{
		return element;
	}
	
	public boolean countable() { return true; }
	
	protected Image entity;
	protected RoomElement element;
	
	public String toString()
	{
		return this.getRoomElement().getRoom().getIndexOfRoomElement(this.getRoomElement()) + "\n";
	}
	
}
