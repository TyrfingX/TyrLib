package tyrfing.common.renderables;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import tyrfing.common.render.TargetMetrics;
import tyrfing.common.struct.Node;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

public class Batch extends Renderable {

	private Canvas canvas;
	private Bitmap bitmap;
	private int height;
	private int width;
	private Queue<Renderable> toRender;
	private boolean redraw;
	
	public Batch(Node parent, int width, int height)
	{
		super();
		this.parent = parent;
		parent.attachObject(this);
		this.canvas = new Canvas();
		this.width = width;
		this.height = height;
		this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		this.canvas.setBitmap(bitmap);
		toRender = new LinkedBlockingQueue<Renderable>();
		redraw = false;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	@Override
	public void onRender(Canvas target, float passedTime) {
		
		if (redraw)
		{
			canvas.drawColor(Color.BLACK);
			for (Renderable r : toRender)
			{
				r.onRender(canvas, 0);
			}
			redraw = false;
		}
		
		float x = parent.getX() * (TargetMetrics.xdpi / 160);
		float y = parent.getY() * (TargetMetrics.ydpi / 160);
		
		target.drawBitmap(bitmap, x,y, paint);
	}
	
	public void addRenderable(Renderable r)
	{
		r.onRender(canvas, 0);
		toRender.add(r);
	}
	
	public void removeRenderable(Renderable r)
	{
		toRender.remove(r);
		this.redraw();
	}
	
	public void redraw()
	{
		redraw = true;
	}
	


}
