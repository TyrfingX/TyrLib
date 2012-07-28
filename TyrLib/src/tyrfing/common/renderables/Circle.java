package tyrfing.common.renderables;

import java.util.ArrayList;

import tyrfing.common.game.objects.Updater;
import tyrfing.common.render.CircleAffector;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.struct.Node;
import android.graphics.Canvas;

public class Circle extends Primitive {
	
	float r;
	private ArrayList<CircleAffector> affectors;

	public Circle(float r, int color)
	{
		super(color);
		this.r = r;
		paint.setAntiAlias(true);
		affectors = new ArrayList<CircleAffector>();
	}
	
	public Circle(float r, int color, Node parent)
	{
		super(color, parent);
		this.r = r;
		paint.setAntiAlias(true);
		affectors = new ArrayList<CircleAffector>();
	}
	
	public void setRadius(float r)
	{
		this.r = r;
	}
	public float getRadius()
	{
		return this.r;
	}
	
	@Override
	public void onRender(Canvas target, float time) {
		if (parent != null)
		{
			float x = parent.getX() * TargetMetrics.xScale;
			float y = parent.getY() * TargetMetrics.yScale;
			target.drawCircle(x, y, r * TargetMetrics.yScale, paint);
		}
	}
	public Circle clone()
	{
		Circle c = null;
		if (parent != null)
			c = new Circle(r, paint.getColor(), parent);
		else
			c = new Circle(r, paint.getColor());
		c.paint.setAlpha(paint.getAlpha());
		
		for (CircleAffector affector : affectors)
		{
			CircleAffector a = affector.clone();
			a.setCircle(c);
			c.affectors.add(a);
		}
		
		
		return c;
	}
	
	public void addAffector(CircleAffector a)
	{
		this.affectors.add(a);
	}
	
	public void playAffectors(Updater executer)
	{
		for (CircleAffector affector : affectors)
		{
			executer.addItem(affector);
		}
	}
	
	
}
