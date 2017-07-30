package tyrfing.common.renderables;

import tyrfing.common.render.TargetMetrics;
import tyrfing.common.struct.Node;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public class Image extends Renderable {
	
	public static final long TOPLEFT = 0;
	public static final long CENTER = 1;
	
	
	protected Bitmap bitmap;
	protected long imagePosition;
	
	protected Rect area;
	
	public Image(Bitmap bitmap)
	{
		super();
		this.bitmap = bitmap;
	}
	
	public Image(Bitmap bitmap, Node parent)
	{
		super();
		parent.attachObject(this);
		this.parent = parent;
		this.bitmap = bitmap;
		
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	
	
	public void onRender(Canvas target, float time)
	{	
		super.onRender(target, time);
		Node p = parent;
		if (p != null)
		{
			
			float x = p.getX() * TargetMetrics.xScale;
			float y = p.getY() * TargetMetrics.yScale;
			
			if (area == null)
			{
				if (imagePosition == Image.CENTER)
					target.drawBitmap(bitmap, x - bitmap.getWidth() * TargetMetrics.xScale * 0.5f,y - bitmap.getHeight() * TargetMetrics.yScale * 0.5f, paint);
				else
					target.drawBitmap(bitmap, x, y, paint);
			}
			else
			{
				RectF area2 = new RectF();
	
				if (imagePosition == Image.CENTER)
				{
					area2.left = (int) (x - area.width() * 0.5f * TargetMetrics.xScale);	
					area2.top = (int) (y - area.height() * 0.5f * TargetMetrics.yScale);
					area2.right = (int) (x + area.width() * 0.5f * TargetMetrics.xScale);
					area2.bottom = (int) (y + area.height() * 0.5f * TargetMetrics.yScale);
				}
				else
				{
					area2.left = (int) x;	
					area2.top = (int) y;
					area2.right = (int) (x + area.width() * TargetMetrics.xScale);
					area2.bottom = (int) (y + area.height() * TargetMetrics.yScale);				
				}
				
				target.drawBitmap(bitmap, area, area2, paint);
			}
		
		}

	}
	
	public void setAlpha(int alpha)
	{
		paint.setAlpha(alpha);
	}
	
	public float getX()
	{
		return parent.getX();
	}
	
	public float getY()
	{
		return parent.getY();
	}
	
	public float getWidth()
	{
		if (area == null)
			return bitmap.getWidth();
		else
			return area.width();
	}
	
	public float getHeight()
	{
		if (area == null)
			return bitmap.getHeight();
		else
			return area.height();
	}
	
	public void setUsedArea(Rect area)
	{
		this.area = area;
	}
	
	public Image clone()
	{
		Image img = null;
		if (parent != null)
			img = new Image(bitmap, parent);
		else
			img = new Image(bitmap);
	
		img.area = area;
		img.imagePosition = this.imagePosition;
		return img;
	}
	
	public void setImageAlignment(long alignment)
	{
		this.imagePosition = alignment;
	}
}
