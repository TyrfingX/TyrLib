package com.tyrfing.games.tyrlib3.input;

import com.tyrfing.games.tyrlib3.input.IKeyboardEvent;

import android.view.KeyEvent;

public class AndroidKeyboardEvent implements IKeyboardEvent {

	private short keyCode;
	private KeyEvent e;
	private int action;
	
	public AndroidKeyboardEvent(int action, short keyCode, KeyEvent e) {
		this.action = action;
		this.keyCode = keyCode;
		this.e = e;
	}
	
	@Override
	public int getAction() {
		return action;
	}

	@Override
	public char getKeyChar() {
		return (char) e.getUnicodeChar();
	}

	@Override
	public short getKeyCode() {
		return keyCode;
	}

	@Override
	public boolean isPrintable() {
		return e.isPrintingKey();
	}

	@Override
	public int getModifiers() {
		return e.getModifiers();
	}
	
}
