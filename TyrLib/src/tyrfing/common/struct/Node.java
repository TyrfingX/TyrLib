package tyrfing.common.struct;

import java.util.ArrayList;

import tyrfing.common.math.Vector2;

public class Node extends Attachable {
	
	
	private float x, y;
	protected ArrayList<Attachable> attachables;
	protected ArrayList<Node> children;
	
	public Node(float x, float y)
	{
		this.x = x;
		this.y = y;
		
		attachables = new ArrayList<Attachable>();
		children = new ArrayList<Node>();
		
	}
	
	public Node()
	{
		this(0,0);
	}
	
	public Node(Vector2 pos)
	{
		this(pos.x, pos.y);
	}

	
	public void attachObject(Attachable attachable)
	{
		attachable.setParent(this);
		attachables.add(attachable);
	}
	
	public void detachObject(Attachable attachable)
	{
		attachable.setParent(null);
		attachables.remove(attachable);
	}

	public void detachObject(int index)
	{
		attachables.get(index).setParent(null);
		attachables.remove(index);
	}

	public Node createChild()
	{
		return this.createChild(0, 0);
	}
	
	public Node createChild(float x, float y)
	{
		Node child = new Node(x, y);
		child.setParent(this);
		children.add(child);
		return child;
	}

	public Node createChild(Vector2 other) {
		return this.createChild(other.x, other.y);
	}
	
	public int countChildren()
	{
		return children.size();
	}
	
	public Node getChild(int index)
	{
		return children.get(index);
	}
	
	public void setX(float x)
	{
		this.x = x;
	}

	
	public void setY(float y)
	{
		this.y = y;
	}
	
	public float getX()
	{
		float pX = 0;
		if (parent != null) pX = parent.getX();
		return this.x + pX;
	}

	public float getY()
	{
		float pY = 0;
		if (parent != null) pY = parent.getY();
		return this.y + pY;
	}
	
	public float getRelativeX()
	{
		return this.x;
	}
	
	public float getRelativeY()
	{
		return this.y;
	}
	
	public Vector2 getPos()
	{
		return new Vector2(x,y);
	}
	
	public void setPos(Vector2 pos)
	{
		this.setX(pos.x);
		this.setY(pos.y);
	}
	
	public void translate(Vector2 trans)
	{
		float x = this.x + trans.x;
		float y = this.y + trans.y;
		this.setPos(new Vector2(x,y));
	}
	
	public Vector2 getAbsolutePos()
	{
		return new Vector2(this.getX(),this.getY());
	}
	
	public void detachAllObjectsRecursivly()
	{
		attachables.clear();
		for (Node node : children)
		{
			node.detachAllObjectsRecursivly();
		}
	}
	
	public void removeChildren()
	{
		children.clear();
	}

	public void removeChild(Node child)
	{
		children.remove(child);
		child.setParent(null);
	}
	
	public void addChild(Node child)
	{
		children.add(child);
		child.setParent(this);
	}
	
}
