package com.tyrfing.games.tyrlib3.input;

public interface IKeyboardListener {
	public boolean onPress(IKeyboardEvent e);
	public boolean onRelease(IKeyboardEvent e);
	public long getPriority();
}
