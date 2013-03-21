package com.tyrlib2.input;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import android.view.MotionEvent;
import android.view.View;

import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.ReversePriorityComparator;

public class InputManager {

	private Queue<ITouchListener> touchListeners;
	private Vector<IBackListener> backListeners;
	private boolean touching = false;
	private Vector2 lastTouch = null;
	private static InputManager instance;
	
	public InputManager()
	{
		touchListeners = new LinkedBlockingQueue<ITouchListener>();
		backListeners = new Vector<IBackListener>();
	}
	
	public static InputManager getInstance() {
		if (instance == null) {
			instance = new InputManager();
		}
		
		return instance;
	}
	
	public void destroy() {
		instance = null;
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
	
		if (actionCode != MotionEvent.ACTION_MOVE) {
		
			PriorityQueue<ITouchListener> queue = new PriorityQueue<ITouchListener>(100, new ReversePriorityComparator());
			for (ITouchListener listener : touchListeners)
			{
				if (listener.isEnabled())
				{
					queue.add(listener);
				}
			}
			
			
			int pid = action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			int id = event.getPointerId(pid);
			
			Vector2 point = new Vector2(event.getX(pid) / v.getWidth(), event.getY(pid) / v.getHeight());
			
			if (actionCode == MotionEvent.ACTION_DOWN || actionCode == MotionEvent.ACTION_POINTER_DOWN)
				touching = true;
			else if (actionCode == MotionEvent.ACTION_UP || actionCode == MotionEvent.ACTION_POINTER_UP)
				touching = false;
			
			lastTouch = new Vector2(point.x, point.y);
			
			while (!queue.isEmpty())
			{
				ITouchListener listener = queue.poll();
				if (actionCode == MotionEvent.ACTION_DOWN || actionCode == MotionEvent.ACTION_POINTER_DOWN)
				{
					if (listener.onTouchDown(point, event, id)) break;
				}
				else if (actionCode == MotionEvent.ACTION_UP || actionCode == MotionEvent.ACTION_POINTER_UP)
				{	
					if (listener.onTouchUp(point, event, id)) break;
				}
			}
		
		} else {
			
			int countPointers = event.getPointerCount();
			for (int i = 0; i < countPointers; ++i) {
				
				PriorityQueue<ITouchListener> queue = new PriorityQueue<ITouchListener>(100, new ReversePriorityComparator());
				for (ITouchListener listener : touchListeners)
				{
					if (listener.isEnabled())
					{
						queue.add(listener);
					}
				}
				
				
				int id = event.getPointerId(i);
				
				Vector2 point = new Vector2(event.getX(i) / v.getWidth(), event.getY(i) / v.getHeight());
				
				lastTouch = new Vector2(point.x, point.y);
				
				while (!queue.isEmpty())
				{
					ITouchListener listener = queue.poll();	
			        if (listener.onTouchMove(point, event, id)) break;
				}
			
			}
			
		}
		
		return true;
	}
	
	public boolean onPressBack()
	{
		for (int i = backListeners.size() - 1; i >= 0; --i) {
			IBackListener listener = backListeners.get(i);
			if (listener.onPressBack()) return true;
		}
		
		return false;
	}
	
	public void addTouchListener(ITouchListener listener)
	{
		touchListeners.add(listener);
	}
	
	public void removeTouchListener(ITouchListener listener)
	{
		touchListeners.remove(listener);
	}
	
	public void addBackListener(IBackListener listener)
	{
		if (!backListeners.contains(listener))
		{
			backListeners.add(listener);
		}
	}
	
	public void removeBackListener(IBackListener listener)
	{
		backListeners.remove(listener);
	}
	
	public boolean isUserTouching()
	{
		return touching;
	}
	
	public Vector2 getLastTouch()
	{
		return lastTouch;
	}
	
	

}
