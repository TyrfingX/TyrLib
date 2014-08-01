package com.TyrLib2.PC.input;

import com.jogamp.newt.event.KeyEvent;
import com.tyrlib2.input.IKeyboardEvent;

public class PCKeyboardEvent implements IKeyboardEvent {
	
	private KeyEvent e;
	private int action;
	
	public PCKeyboardEvent(KeyEvent e, int action) {
		this.e = e;
		this.action = action;
	}

	@Override
	public int getAction() {
		return action;
	}

	@Override
	public char getKeyChar() {
		return e.getKeyChar();
	}
	
	@Override
	public short getKeyCode() {
		return e.getKeyCode();
	}
	
	@Override
	public boolean isPrintable() {
		return e.isPrintableKey();
	}

	@Override
	public int getModifiers() {
		return e.getModifiers();
	}
}
