package tyrfing.common.struct;


public abstract class Attachable {
	
	protected Node parent;
	public void setParent(Node parent)
	{
		this.parent = parent;
	}
	
	public Node getParent()
	{
		return parent;
	}
	
	public void detach()
	{
		if (parent != null)
		{
			parent.detachObject(this);
		}
			
	}
	
}
