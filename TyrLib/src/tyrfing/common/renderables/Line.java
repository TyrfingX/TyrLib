package tyrfing.common.renderables;

import android.graphics.Canvas;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.struct.Node;

public class Line extends Primitive {

	private float toX, toY;

	public Line(float toX, float toY, int color) {
		super(color);
		this.toX = toX;
		this.toY = toY;
	}
	
	public Line(float toX, float toY, int color, Node parent) {
		super(color, parent);
		this.toX = toX;
		this.toY = toY;
	}

	
	public void setTo(Vector2 to)
	{
		this.toX = to.x;
		this.toY = to.y;
	}
	
	public Vector2 getTo()
	{
		return new Vector2(toX, toY);
	}
	
	@Override
	public void onRender(Canvas target, float time) {
		super.onRender(target, time);
		float x = parent.getX() * TargetMetrics.xScale;
		float y = parent.getY() * TargetMetrics.yScale;
		target.drawLine(x, y, x + toX * TargetMetrics.xScale, y + toY * TargetMetrics.yScale, paint);
	}

}
