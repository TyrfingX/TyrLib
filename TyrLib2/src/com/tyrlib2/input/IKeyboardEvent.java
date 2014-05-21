package com.tyrlib2.input;

public interface IKeyboardEvent {
	public static int ACTION_PRESSED = 0;
	public static int ACTION_RELEASED = 1;
	
	public int getAction();
	public char getKeyChar();
	public short getKeyCode();
	boolean isPrintable();
}
