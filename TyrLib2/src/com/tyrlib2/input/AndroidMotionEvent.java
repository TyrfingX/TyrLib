package com.tyrlib2.input;

import android.view.MotionEvent;

public class AndroidMotionEvent implements IMotionEvent {

	private MotionEvent event;
	
	public AndroidMotionEvent(MotionEvent event) {
		this.event = event;
	}
	
	@Override
	public int getAction() {
		return event.getAction();
	}

	@Override
	public int getPointerId(int pointer) {
		return event.getPointerId(pointer);
	}

	@Override
	public float getX(int id) {
		return event.getX(id);
	}

	@Override
	public float getY(int id) {
		return event.getY(id);
	}

	@Override
	public int getPointerCount() {
		return event.getPointerCount();
	}

}
