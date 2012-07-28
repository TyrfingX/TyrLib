package tyrfing.common.game.objects;

import java.util.Observable;
import java.util.Observer;

import tyrfing.common.math.Vector2;
import tyrfing.common.struct.Node;

public abstract class GameObject extends Observable implements IUpdateable, Observer {
	
	protected Node node;
	
	public GameObject(Node node)
	{
		this.node = node;
	}
	
	public void setX(float x)
	{
		node.setX(x);
	}
	
	public void setY(float y)
	{
		node.setY(y);
	}
	
	public float getWidth() { return 0; }
	public float getHeight() { return 0; }

	
	public Node getNode()
	{
		return node;
	}
	
	public void setNode(Node node)
	{
		this.node = node;
	}
	
	public Vector2 getSize()
	{
		return new Vector2(this.getWidth(), this.getHeight());
	}
	
	public Vector2 getCenter()
	{
		Vector2 pos = this.getAbsolutePos();
		pos.x += this.getWidth() / 2;
		pos.y += this.getHeight() / 2;
		
		return pos;		
	}
	
	@Override
	public boolean isFinished() {
		return false;
	}
	
	public void translate(float x, float y)
	{
		node.translate(new Vector2(x,y));
	}
	
	public float getX()
	{
		return node.getX();
	}
	
	public float getY()
	{
		return node.getY();
	}
	
	public Vector2 getAbsolutePos()
	{
		return node.getAbsolutePos();
	}
	
	public Vector2 getPos()
	{
		return node.getPos();
	}
	
	public void setPos(Vector2 pos)
	{
		node.setPos(pos);
	}
	
	public void translate(Vector2 translation)
	{
		this.translate(translation);
	}
	
	@Override
	public void update(Observable observable, Object data) {	
	}

}
