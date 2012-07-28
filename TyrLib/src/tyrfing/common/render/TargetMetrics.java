package tyrfing.common.render;

import tyrfing.common.math.Vector2;
import android.app.Activity;
import android.util.DisplayMetrics;


public class TargetMetrics {
	
	public static float xdpi;
	public static float ydpi;
	public static float width;
	public static float height;
	public static float xScale;
	public static float yScale;
	public static int fontSize;
	
	
	public static void init(Activity activity)
	{
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		//xdpi = 160;
		//ydpi = 160;
		
        xdpi = dm.xdpi;
        ydpi = dm.ydpi;
        
        width = dm.widthPixels;
		height = dm.heightPixels;
		
		
		if (width >= 400)
		{
			fontSize = 15;
		}
		else
		{
			fontSize = 11;
		}
		
		if (xdpi <= 160) 
		{
			fontSize -= 3;
		} else if (xdpi > 240)
		{
			fontSize += 4;
		}
		
		xdpi = 160;
		ydpi = 160;
	
	
		xScale = TargetMetrics.xdpi / 160;
		yScale = TargetMetrics.ydpi / 160;

		
		
		
	}
	
	public static boolean inWindow(Vector2 point)
	{
		
		Vector2 p = new Vector2(point.x * (xdpi /160), point.y * (ydpi/160));
		
		if (p.x >= 0 && p.x <= width)
		{
			if (p.y >= 0 && p.y <= height)
			{
				return true;
			}
		}
		
		return false;
	}
	
}
