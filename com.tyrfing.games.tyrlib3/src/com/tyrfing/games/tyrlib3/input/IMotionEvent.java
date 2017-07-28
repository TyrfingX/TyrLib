package com.tyrfing.games.tyrlib3.input;

public interface IMotionEvent {
	
	public static final int ACTION_MASK = 0xff;
	public static final int ACTION_MOVE = 0x2;
	public static final int ACTION_POINTER_INDEX_SHIFT = 0x8;
	public static final int ACTION_DOWN = 0x0;
	public static final int ACTION_UP = 0x1;
	public static final int ACTION_POINTER_DOWN = 0x5;
	public static final int ACTION_POINTER_UP = 0x6;
	
	public int getAction();
	public int getPointerId(int pointer);
	public float getX(int id);
	public float getY(int id);
	public int getPointerCount();
	public float getRotation();
	public int getButton();
}
