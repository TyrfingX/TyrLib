package com.tyrlib2.gui;

import java.util.HashMap;
import java.util.Map;

import com.tyrlib2.game.Updater;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

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
	
	public static final long GUI_BASE_PRIORITY = 10000;
	public static final long GUI_OVERLAY_PRIORITY = 1000000;
	
	public WindowManager() {

		windows = new HashMap<String, Window>();
		
		updater = new Updater();

		if (SceneManager.getInstance().getRenderer() != null) {
		
			SceneManager.getInstance().addFrameListener(updater);
			rootNode = SceneManager.getInstance().getRootSceneNode().createChild(new Vector3(0,SceneManager.getInstance().getViewport().getHeight(),0));
			
			renderer = new GUIRenderer();
			SceneManager.getInstance().getRenderer().addRenderable(renderer, OpenGLRenderer.OVERLAY_CHANNEL);
		
		}
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
		window.destroy();
	}
	
	public void addWindow(Window window) {
		rootNode.attachChild(window.node);
		renderer.addWindow(window);
		windows.put(window.getName(), window);
		updater.addItem(window);
		
		notifyResort();
	}
	
	public Window createWindow(String name, Vector2 size) {
		Window window = new Window(name, size);
		addWindow(window);
		return window;
	}
	
	public Window createWindow(String name, Vector2 pos, Vector2 size) {
		Window window = new Window(name, size);
		addWindow(window);
		window.setRelativePos(pos);
		return window;
	}
	
	public Window createLabel(String name, Vector2 pos, String text) {
		Label label = new Label(name, pos, text);
		addWindow(label);
		return label;
	}
	
	public Button createButton(String name, Vector2 pos, Vector2 size, String text) {
		Button button = new Button(name, pos, size, text);
		addWindow(button);
		return button;
	}
	
	public Frame createFrame(String name, Vector2 pos, Vector2 size) {
		Frame frame = new Frame(name, pos, size);
		addWindow(frame);
		return frame;
	}
	
	public Window createImageBox(String name, Vector2 pos, String atlasName, String atlasRegion, Vector2 size) {
		ImageBox imageBox = new ImageBox(name, pos, atlasName, atlasRegion, size);
		addWindow(imageBox);
		return imageBox;
	}
	
	public Window createImageBox(String name, Vector2 pos, String textureName, Vector2 size) {
		ImageBox imageBox = new ImageBox(name, pos, textureName, size);
		addWindow(imageBox);
		return imageBox;
	}
	
	public Window createOverlay(String name, Vector2 pos, Vector2 size, Color color) {
		Overlay overlay = new Overlay(name, pos, size, color);
		overlay.setPriority(GUI_OVERLAY_PRIORITY);
		addWindow(overlay);
		return overlay;
	}
	
	public Window createItemList(String name, Vector2 pos, Vector2 size, float padding, int displayItems) {
		ItemList itemList = new ItemList(name, pos, size, padding, displayItems);
		addWindow(itemList);
		return itemList;
	}
	
	public Window createOverlay(String name, Color color) {
		Window overlay = createOverlay(name, new Vector2(0,0), new Vector2(1,1), color);
		return overlay;
	}
	
	/**
	 * This method turns a window into a popup hovering over all other windows
	 * @param window
	 * @param callback	Called when the popup is closed
	 * @return
	 */
	
	public Window createPopup(Window window) {
		Skin skin = WindowManager.getInstance().getSkin();
		Window overlay = createOverlay("PopupOverlay/" + window.getName(), skin.OVERLAY_COLOR);
		overlay.addChild(window);
		overlay.setVisible(false);
		overlay.setAlpha(0);
		overlay.setMaxAlpha(skin.OVERLAY_MAX_ALPHA);
		InputManager.getInstance().sort();
		return overlay;
	}
	
	public Window createConfirmMessageBox(String name, String text, final IEventListener callback) {
		Skin skin = getSkin();
		Window frame = createFrame(name, new Vector2(skin.MESSAGE_BOX_X, skin.MESSAGE_BOX_Y), new Vector2(skin.MESSAGE_BOX_W, skin.MESSAGE_BOX_H));
		Window button = createButton(	name + "/ConfirmButton", 
										new Vector2(skin.MESSAGE_BOX_W/2 - skin.MESSAGE_BOX_W*skin.MESSAGE_BOX_BUTTON_W/2, skin.MESSAGE_BOX_H - skin.MESSAGE_BOX_H*(skin.MESSAGE_BOX_BUTTON_H - skin.MESSAGE_BOX_BUTTON_PAD_Y)),
										new Vector2(skin.MESSAGE_BOX_W*skin.MESSAGE_BOX_BUTTON_W, skin.MESSAGE_BOX_H*skin.MESSAGE_BOX_BUTTON_H),
										"Confirm");
		frame.addChild(button);
		Label label = (Label)createLabel(name + "/Label", new Vector2(skin.MESSAGE_BOX_W*skin.MESSAGE_BOX_LABEL_X, skin.MESSAGE_BOX_H*skin.MESSAGE_BOX_LABEL_Y), text);
		label.setBgColor(Color.TRANSPARENT.copy());
		frame.addChild(label);
		Window popup = createPopup(frame);
		
		button.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				Window messageBox = event.getSource().getParent().getParent();
				if (callback != null) {
					callback.onEvent(new WindowEvent(messageBox, WindowEventType.CONFIRMED));
				}
			}
		});
		return popup;
	}
	
	public Window createTooltip(String name, Vector2 size) {
		Window tooltip = new Tooltip(name, size);
		tooltip.setPriority(GUI_OVERLAY_PRIORITY);
		addWindow(tooltip);
		return tooltip;
	}
	
	public Window createProgressBar(String name, Vector2 pos, Vector2 size, float maxProgress) {
		Window progressBar = new ProgressBar(name, pos, size, maxProgress);
		addWindow(progressBar);
		return progressBar;
	}
	
	protected void removeWindow(Window window) {
		renderer.removeWindow(window);
		windows.remove(window);
		window.node.detach();
		InputManager.getInstance().removeTouchListener(window);
	}
	
	public Window getWindow(String name) {
		return windows.get(name);
	}
	
	protected SceneNode getRootNode() {
		return rootNode;
	}

	protected void notifyResort() {
		renderer.notifyResort();
	}
}
	