package com.TyrLib2.PC.input;

import com.jogamp.newt.event.MouseEvent;
import com.tyrlib2.input.IMotionEvent;

public class PCMotionEvent implements IMotionEvent {

	private MouseEvent e;
	private int action;
	
	public PCMotionEvent(MouseEvent e, int action) {
		this.e = e;
		this.action = action;
	}
	
	@Override
	public int getAction() {
		return action;
	}

	@Override
	public int getPointerId(int pointer) {
		return e.getPointerId(pointer);
	}

	@Override
	public float getX(int id) {
		return e.getX(id);
	}

	@Override
	public float getY(int id) {
		return e.getY(id);
	}

	@Override
	public int getPointerCount() {
		return e.getPointerCount();
	}

}
