package tyrfing.common.render;

import tyrfing.common.game.objects.IUpdateable;
import tyrfing.common.renderables.Circle;

public class CircleAffector implements IUpdateable {
	private Circle c;
	
	private float scaleEnd;
	private float speed;;
	private float scaleStart;
	
	public CircleAffector(Circle c)
	{
		this();
		this.c = c;
	}
	
	public CircleAffector()
	{
		this.scaleEnd = -1;
	}
	
	public void setScaler(float start, float end, float time)
	{
		this.scaleEnd = end;
		this.scaleStart = start;
		c.setRadius(start);
		this.speed = (end - start) / time;
	}
	
	public void setCircle(Circle c)
	{
		this.c = c;
		if (scaleStart != 0)
		{
			c.setRadius(scaleStart);
		}
	}

	@Override
	public void onUpdate(float time) {
		
		if (scaleEnd != -1)
		{
			float r = c.getRadius();
			r += speed * time;
			if (r >= scaleEnd && speed >= 0)
			{
				c.setRadius(scaleEnd);
				scaleEnd = -1;
			}
			else if (r <= scaleEnd && speed <= 0)
			{
				c.setRadius(scaleEnd);
				scaleEnd = -1;
			}
			else
			{
				c.setRadius(r);
			}
		}	
	}

	@Override
	public boolean isFinished() {
		boolean finished;
		finished = (scaleEnd == -1);
		return finished;
	}
	
	public CircleAffector clone()
	{
		CircleAffector a = new CircleAffector();
		a.scaleStart = scaleStart;
		a.scaleEnd = scaleEnd;
		a.speed = speed;
		a.setCircle(c);
		return a;
	}
}
