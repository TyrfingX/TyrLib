package tyrfing.common.renderables;

import tyrfing.common.struct.Node;
import android.graphics.Paint;

public abstract class Primitive extends Renderable {

	
	
	public Primitive(int color)
	{
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(color);
	}
	
	public Primitive(int color, Node parent)
	{
		this(color);
		parent.attachObject(this);
	}

	public void setColor(int color)
	{
		paint.setColor(color);
	}
	
	public int getColor()
	{
		return paint.getColor();
	}
	
	public float getX()
	{
		return parent.getX();
	}
	
	public float getY()
	{
		return parent.getY();
	}
	
	
}
