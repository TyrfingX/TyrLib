package com.tyrlib2.gui;

import java.util.HashMap;
import java.util.Map;

public class WindowEvent {
	public enum WindowEventType {
		FADE_IN_FINISHED,
		FADE_OUT_FINISHED,
		MOVEMENT_FINISHED,
		TOUCH_ENTERS,
		TOUCH_LEAVES,
		TOUCH_MOVES,
		TOUCH_DOWN,
		TOUCH_UP,
		MOUSE_ENTERS,
		MOUSE_LEAVES,
		DESTROYED,
		CONFIRMED,
		TOUCH, 
		CALL_LINK, 
		SIZE_CHANGED, 
		FADE_IN_STARTED,
		FADE_OUT_STARTED,
		ALPHA_CHANGED,
		VISIBILITY_CHANGED, 
		PAUSE_FINISHED
	}
	
	private WindowEventType type;
	private Window source;
	private Map<String, Object> params;
	
	public WindowEvent(Window source, WindowEventType type) {
		this.type = type;
		this.source = source;
		params = new HashMap<String, Object>();
	}
	
	public WindowEventType getType() {
		return type;
	}
	
	public Object getParam(String param) {
		return params.get(param);
	}
	
	public void setParam(String param, Object value) {
		params.put(param, value);
	}
	
	public Window getSource() {
		return source;
	}
}
