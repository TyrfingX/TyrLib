package tyrfing.common.input;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import tyrfing.common.math.Vector2;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.struct.ReversePriorityComparator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class InputManager implements OnTouchListener{

	private static Queue<TouchListener> touchListeners;
	private static Vector<BackListener> backListeners;
	private static boolean touching = false;
	private static Vector2 lastTouch = null;
	
	public InputManager()
	{
		touchListeners = new LinkedBlockingQueue<TouchListener>();
		backListeners = new Vector<BackListener>();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Vector2 point = new Vector2(event.getX() / (TargetMetrics.xdpi / 160), event.getY() / (TargetMetrics.xdpi / 160));
		
		if (event.getAction() == MotionEvent.ACTION_DOWN)
			touching = true;
		else if (event.getAction() == MotionEvent.ACTION_UP)
			touching = false;
		
		lastTouch = new Vector2(point.x, point.y);
		
		PriorityQueue<TouchListener> queue = new PriorityQueue<TouchListener>(100, new ReversePriorityComparator());
		for (TouchListener listener : touchListeners)
		{
			if (listener.isEnabled())
			{
				queue.add(listener);
			}
		}
		
		while (!queue.isEmpty())
		{
			TouchListener listener = queue.poll();
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				if (listener.onTouchDown(point)) break;
			}
			else if (event.getAction() == MotionEvent.ACTION_UP)
			{	
				if (listener.onTouchUp(point)) break;
			}
			else if (event.getAction() == MotionEvent.ACTION_MOVE)
			{	
				if (listener.onTouchMove(point)) break;
			}
		}
		
		return true;
	}
	
	public static boolean onPressBack()
	{
		for (int i = backListeners.size() - 1; i >= 0; --i) {
			BackListener listener = backListeners.get(i);
			if (listener.onPressBack()) return true;
		}
		
		return false;
	}
	
	public static void addTouchListener(TouchListener listener)
	{
		touchListeners.add(listener);
	}
	
	public static void removeTouchListener(TouchListener listener)
	{
		touchListeners.remove(listener);
	}
	
	public static void addBackListener(BackListener listener)
	{
		if (!backListeners.contains(listener))
		{
			backListeners.add(listener);
		}
	}
	
	public static void removeBackListener(BackListener listener)
	{
		backListeners.remove(listener);
	}
	
	public static boolean isUserTouching()
	{
		return touching;
	}
	
	public static Vector2 getLastTouch()
	{
		return lastTouch;
	}
	
	

}
