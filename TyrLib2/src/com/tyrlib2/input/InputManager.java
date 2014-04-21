package com.tyrlib2.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.ReversePriorityComparator;

public class InputManager {

	public static final long FOCUS_PRIORITY = 2000000;
	
	private List<ITouchListener> touchListeners;
	private Vector<IBackListener> backListeners;
	private boolean touching = false;
	private Vector2 lastTouch = null;
	private static InputManager instance;
	
	private boolean sort;
	
	public InputManager()
	{
		touchListeners = new ArrayList<ITouchListener>();
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
	
	public boolean onTouch(IView v, IMotionEvent event) {
		int action = event.getAction();
		int actionCode = action & IMotionEvent.ACTION_MASK;
		
		if (sort) {
			Collections.sort(touchListeners, new ReversePriorityComparator());
			sort = false;
		}
		
		if (actionCode != IMotionEvent.ACTION_MOVE) {
			
			int pid = action >> IMotionEvent.ACTION_POINTER_INDEX_SHIFT;
			int id = event.getPointerId(pid);
			
			Vector2 point = new Vector2(event.getX(pid) / v.getWidth(), event.getY(pid) / v.getHeight());
			
			if (actionCode == IMotionEvent.ACTION_DOWN || actionCode == IMotionEvent.ACTION_POINTER_DOWN)
				touching = true;
			else if (actionCode == IMotionEvent.ACTION_UP || actionCode == IMotionEvent.ACTION_POINTER_UP)
				touching = false;
			
			lastTouch = new Vector2(point.x, point.y);
			
			for (int i = 0; i < touchListeners.size(); ++i) {
				ITouchListener listener = touchListeners.get(i);
				if (listener.isEnabled() && (actionCode == IMotionEvent.ACTION_DOWN || actionCode == IMotionEvent.ACTION_POINTER_DOWN))
				{
					if (listener.onTouchDown(point, event, id)) break;
				}
				else if (listener.isEnabled() && (actionCode == IMotionEvent.ACTION_UP || actionCode == IMotionEvent.ACTION_POINTER_UP))
				{	
					if (listener.onTouchUp(point, event, id)) break;
				}
			}
		
		} else {
			
			int countPointers = event.getPointerCount();
			for (int i = 0; i < countPointers; ++i) {
				
				int id = event.getPointerId(i);
				
				Vector2 point = new Vector2(event.getX(i) / v.getWidth(), event.getY(i) / v.getHeight());
				
				lastTouch = new Vector2(point.x, point.y);
				
				for (int j = 0; j < touchListeners.size(); ++j) {
					ITouchListener listener = touchListeners.get(j);	
			        if (listener.isEnabled()) {
			        	if (listener.onTouchMove(point, event, id)) break;
			        }
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
		for (int i = 0; i < touchListeners.size(); ++i) {
			ITouchListener other = touchListeners.get(i);
			if (other.getPriority() <= listener.getPriority()) {
				touchListeners.add(i, listener);
				return;
			}
		}
		
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
	
	public boolean isAdded(ITouchListener touchListener) {
		return touchListeners.contains(touchListener);
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
	
	public void sort() {
		sort = true;
	}
	
	

}
