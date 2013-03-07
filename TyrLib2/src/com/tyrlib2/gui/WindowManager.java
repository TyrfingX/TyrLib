package com.tyrlib2.gui;

import java.util.HashMap;
import java.util.Map;

import com.tyrlib2.game.Updater;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;

/**
 * Manages the life times of windows
 * @author Sascha
 *
 */

public class WindowManager {
	private static WindowManager instance;
	
	private Map<String, Window> windows;
	private Updater updater;
	private SceneNode rootNode;
	
	public WindowManager() {
		windows = new HashMap<String, Window>();
		updater = new Updater();
		SceneManager.getInstance().addFrameListener(updater);
		rootNode = SceneManager.getInstance().getRootSceneNode().createChild();
	}
	
	public static WindowManager getInstance() {
		if (instance == null) {
			instance = new WindowManager();
		}
		
		return instance;
	}
	
	public void destroyWindow(String name) {
		destroyWindow(getWindow(name));
	}
	
	public void destroyWindow(Window window) {
		
	}
	
	protected void removeWindow(Window window) {
		windows.remove(window);
	}
	
	public Window getWindow(String name) {
		return windows.get(name);
	}
	
	protected SceneNode getRootNode() {
		return rootNode;
	}
}
