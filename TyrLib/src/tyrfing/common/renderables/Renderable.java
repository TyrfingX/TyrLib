package tyrfing.common.renderables;

import tyrfing.common.game.objects.Updater;
import tyrfing.common.math.Vector2;
import tyrfing.common.struct.Attachable;
import tyrfing.common.struct.Prioritizable;
import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class Renderable extends Attachable implements Prioritizable  {
	
	private boolean visible;
	private long priority;
	protected Paint paint;
	
	private Vector2 fade;
	private float speed;
	private int alpha;
	
	public Renderable()
	{
		this.visible = true;
		this.paint = new Paint();
	}
	
	public void onRender(Canvas target, float passedTime)
	{
		if (fade != null)
		{
			Vector2 trans = new Vector2(0,0);
			trans.x += fade.x * passedTime;
			trans.y += fade.y * passedTime;
			alpha -= speed * passedTime;
			if ((alpha <= 0 && speed >= 0) || (alpha >= 255 && speed <= 0))
			{
				fade = null;
				if (speed >= 0)
				{
					alpha = 0;
					this.setVisible(false);
				}
				else
				{
					alpha = 255;
				}
			}
			paint.setAlpha(alpha);
			
			this.parent.translate(trans);
			
		}
	}
	
	public void fadeOut(Vector2 fade, float time)
	{
		this.fade = fade;
		speed = 255 / time;
		alpha = 255;
	}
	
	public void blendIn(Vector2 blend, float time)
	{
		this.fade = blend;
		speed = -255 / time;
		alpha = 0;
		this.setVisible(true);
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean getVisible() {
		return this.visible;
	}
	
	public long getPriority()
	{
		return priority;
	}
	
	public void setPriority(long priority)
	{
		this.priority = priority;
	}
	
	public Renderable clone()
	{
		return null;
	}
	
	public Paint getPaint()
	{
		return paint;
	}
	
	public void playAffectors(Updater executer)
	{
		
	}
	
}
