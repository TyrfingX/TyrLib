package com.TyrLib2.PC.main;

import com.tyrlib2.math.Vector2;

public class Config {
	
	public enum ScreenState {
		FULLSCREEN,
		WINDOWED
	}
	
	public ScreenState screenState;
	public Vector2 screenSize;
}
