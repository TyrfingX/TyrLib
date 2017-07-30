package tyrfing.common.renderables;

import android.graphics.Canvas;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.struct.Node;

public class Rectangle extends Primitive {
	
	
	private float width;
	private float height;
	private float scaledWidth;
	private float scaledHeight;

	public Rectangle(float width, float height, int color)
	{
		super(color);
		this.width = width;
		this.height = height;
		this.scaledWidth = width * TargetMetrics.xScale;
		this.scaledHeight = height * TargetMetrics.yScale;
	}
	
	public Rectangle(float width, float height, int color, Node parent)
	{
		super(color, parent);
		this.width = width;
		this.height = height;
		this.scaledWidth = width * TargetMetrics.xScale;
		this.scaledHeight = height * TargetMetrics.yScale;
	}
	
	public void setWidth(float width)
	{
		this.width = width;
		this.scaledWidth = width * TargetMetrics.xScale;
	}


	public void setHeight(float height)
	{
		this.height = height;
		this.scaledHeight = height * TargetMetrics.yScale;
	}

	public float getWidth()
	{
		return this.width;
	}


	public float getHeight()
	{
		return this.height;
	}
	
	public Vector2 getSize() {
		return new Vector2(width, height);
	}
	
	public Vector2 getCenter() {
		return this.getPos().add(this.getSize().multiply(0.5f));
	}
	
	@Override
	public void onRender(Canvas target, float times) {
		super.onRender(target, times);
		float x = parent.getX() * TargetMetrics.xScale;
		float y = parent.getY() * TargetMetrics.yScale;
		target.drawRect(x, y, x + scaledWidth, y + scaledHeight, paint);
	}
	
	public Rectangle clone()
	{
		if (parent != null)
			return new Rectangle(width, height, paint.getColor(), parent);
		else
			return new Rectangle(width, height, paint.getColor());
	}
	

}
