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
	private List<IKeyboardListener> keyListeners;;
	private List<IBackListener> backListeners;
	private List<IScrollListener> scrollListeners;
	private List<IMoveListener> moveListeners;
	private boolean touching = false;
	private Vector2 lastTouch = null;
	private static InputManager instance;
	
	public static int VK_ENTER;
	public static int VK_BACK_SPACE;
	public static int VK_V;
	public static int VK_ESC;
	public static int CTRL_MASK;
	public static int VK_SPACE;
	public static int VK_PLUS;
	public static int VK_MINUS;
	public static int VK_UP;
	public static int VK_DOWN;
	public static int VK_LEFT;
	public static int VK_RIGHT;
	public static int VK_ONE;
	
	private boolean sort;
	
	private List<Short> pressedKeys = new ArrayList<Short>();
	
	public InputManager()
	{
		touchListeners = new ArrayList<ITouchListener>();
		keyListeners = new ArrayList<IKeyboardListener>();
		backListeners = new Vector<IBackListener>();
		scrollListeners = new Vector<IScrollListener>();
		moveListeners = new Vector<IMoveListener>();
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
	
	public boolean onScroll(IView v, IMotionEvent event) {
		
		float rotation = event.getRotation();
		
		for (int i = 0; i < scrollListeners.size(); ++i) {
			if (scrollListeners.get(i).onScroll(rotation)) break;
		}
		
		return false;
	}
	
	public void addScrollListener(IScrollListener listener) {
		scrollListeners.add(0, listener);
	}
	
	public void removeScrollListener(IScrollListener listener) {
		scrollListeners.remove(listener);
	}
	
	public boolean onMove(IView v, IMotionEvent event) {
		
		int action = event.getAction();
		int pid = action >> IMotionEvent.ACTION_POINTER_INDEX_SHIFT;
		
		Vector2 point = new Vector2(event.getX(pid) / v.getWidth(), event.getY(pid) / v.getHeight());
		
		for (int i = 0; i < moveListeners.size(); ++i) {
			if (moveListeners.get(i).isEnabled()) {
				if (moveListeners.get(i).onMove(point)) break;
			}
		}
		
		return false;
	}
	
	public void addMoveListener(IMoveListener listener) {
		moveListeners.add(0, listener);
	}
	
	public void removeMoveListener(IMoveListener listener) {
		moveListeners.remove(listener);
	}
	
	public boolean onKeyEvent(IKeyboardEvent e) {
		
		int action = e.getAction();
		
		if (action == IKeyboardEvent.ACTION_PRESSED && !pressedKeys.contains(e.getKeyCode())) {
			pressedKeys.add(new Short(e.getKeyCode()));
		} else if (action == IKeyboardEvent.ACTION_RELEASED) {
			pressedKeys.remove(new Short(e.getKeyCode()));
		}
		
		for (int i = 0; i < keyListeners.size(); ++i) {
			if (action == IKeyboardEvent.ACTION_PRESSED) {
				if (keyListeners.get(i).onPress(e)) break;
			} else if (action == IKeyboardEvent.ACTION_RELEASED) {
				if (keyListeners.get(i).onRelease(e)) break;
			}
		}
		
		return false;
	}
	
	public void addKeyboardListener(IKeyboardListener listener) {
		for (int i = 0; i < keyListeners.size(); ++i) {
			IKeyboardListener other = keyListeners.get(i);
			if (other.getPriority() <= listener.getPriority()) {
				keyListeners.add(i, listener);
				return;
			}
		}
		keyListeners.add(listener);
	}
	
	public void removeKeyboardListener(IKeyboardListener listener) {
		keyListeners.remove(listener);
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

	public void onEnterMouse(IView v, IMotionEvent event) {
		for (int i = 0; i < moveListeners.size(); ++i) {
			if (moveListeners.get(i).onEnterRenderWindow()) break;
		}
	}
	
	public void onExitMouse(IView v, IMotionEvent event) {
		for (int i = 0; i < moveListeners.size(); ++i) {
			if (moveListeners.get(i).onLeaveRenderWindow()) break;
		}
	}

	public void onGainFocus(IView v) {
		for (int i = 0; i < moveListeners.size(); ++i) {
			if (moveListeners.get(i).onRenderWindowGainFocus()) break;
		}
	}
	
	public void onLoseFocus(IView v) {
		for (int i = 0; i < moveListeners.size(); ++i) {
			if (moveListeners.get(i).onRenderWindowLoseFocus()) break;
		}
	}
	
	public boolean isKeyPressed(short key) {
		return pressedKeys.contains(key);
	}
	
	

}
