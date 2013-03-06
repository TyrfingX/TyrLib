package com.tyrlib2.gui;

/**
 * Manages the life times of windows
 * @author Sascha
 *
 */

public class WindowManager {
	private static WindowManager instance;
	
	public static WindowManager getInstance() {
		if (instance == null) {
			instance = new WindowManager();
		}
		
		return instance;
	}
	
	public void destroyWindow(Window window) {
		
	}
}
