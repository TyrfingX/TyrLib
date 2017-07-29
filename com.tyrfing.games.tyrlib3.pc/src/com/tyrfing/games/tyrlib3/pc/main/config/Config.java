package com.tyrfing.games.tyrlib3.pc.main.config;

import com.tyrfing.games.tyrlib3.model.math.Vector2F;

public class Config {
	
	public enum ScreenState {
		FULLSCREEN,
		WINDOWED
	}
	
	public ScreenState screenState;
	public Vector2F screenSize;
}
