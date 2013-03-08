package com.tyrlib2.gui;

import java.util.HashMap;
import java.util.Map;

import com.tyrlib2.game.Updater;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Vector2;

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
	private GUIRenderer renderer;
	
	private Skin skin;
	
	public WindowManager() {
		windows = new HashMap<String, Window>();
		updater = new Updater();
		SceneManager.getInstance().addFrameListener(updater);
		rootNode = SceneManager.getInstance().getRootSceneNode().createChild();
		
		renderer = new GUIRenderer();
		SceneManager.getInstance().getRenderer().addRenderable(renderer, OpenGLRenderer.OVERLAY_CHANNEL);
	}
	
	public void destroy() {
		renderer = null;
		windows.clear();
		skin = null;
		instance = null;
	}
	
	public void loadSkin(Skin skin) {
		this.skin = skin;
	}
	
	public Skin getSkin() {
		return skin;
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
		renderer.removeWindow(window);
		windows.remove(window);
		updater.removeItem(window);
		window.node.detach();
	}
	
	private void addWindow(Window window) {
		rootNode.attachChild(window.node);
		renderer.addWindow(window);
		windows.put(window.getName(), window);
		updater.addItem(window);
	}
	
	public Window createWindow(String name, Vector2 size) {
		Window window = new Window(name, size);
		addWindow(window);
		return window;
	}
	
	public Window createLabel(String name, Vector2 pos, String text) {
		Label label = new Label(name, pos, text);
		addWindow(label);
		return label;
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
