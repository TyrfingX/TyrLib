package com.TyrLib2.PC.input;

import com.jogamp.newt.event.MouseEvent;
import com.tyrlib2.input.IMotionEvent;
import com.tyrlib2.math.Vector3;

public class PCMotionEvent implements IMotionEvent {

	private MouseEvent e;
	private int action;
	
	public PCMotionEvent(MouseEvent e) {
		this.e = e;
	}
	
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

	@Override
	public float getRotation() {
		return (e.getRotation()[0] + e.getRotation()[1] + e.getRotation()[2]) * e.getRotationScale();
	}

	@Override
	public int getButton() {
		return e.getButton()-1;
	}

}
