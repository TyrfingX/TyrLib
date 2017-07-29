package com.tyrfing.games.tyrlib3.view.gui;

import java.util.HashMap;
import java.util.Map;

import com.tyrfing.games.tyrlib3.edit.input.InputManager;
import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.model.game.Updater;
import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneNode;
import com.tyrfing.games.tyrlib3.model.math.Vector2F;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;
import com.tyrfing.games.tyrlib3.view.graphics.Viewport;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.Rectangle2;
import com.tyrfing.games.tyrlib3.view.graphics.renderer.OpenGLRenderer;
import com.tyrfing.games.tyrlib3.view.gui.events.IEventListener;
import com.tyrfing.games.tyrlib3.view.gui.events.WindowEvent;
import com.tyrfing.games.tyrlib3.view.gui.events.WindowEvent.WindowEventType;
import com.tyrfing.games.tyrlib3.view.gui.layout.Paint;
import com.tyrfing.games.tyrlib3.view.gui.layout.ScaledVector2;
import com.tyrfing.games.tyrlib3.view.gui.layout.Skin;
import com.tyrfing.games.tyrlib3.view.gui.widgets.Button;
import com.tyrfing.games.tyrlib3.view.gui.widgets.Frame;
import com.tyrfing.games.tyrlib3.view.gui.widgets.ImageBox;
import com.tyrfing.games.tyrlib3.view.gui.widgets.ItemList;
import com.tyrfing.games.tyrlib3.view.gui.widgets.Label;
import com.tyrfing.games.tyrlib3.view.gui.widgets.Overlay;
import com.tyrfing.games.tyrlib3.view.gui.widgets.ParticleWindow;
import com.tyrfing.games.tyrlib3.view.gui.widgets.ProgressBar;
import com.tyrfing.games.tyrlib3.view.gui.widgets.Tooltip;
import com.tyrfing.games.tyrlib3.view.gui.widgets.Window;

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
	
	protected Vector2F[] scales = { new Vector2F(1,1) };
	
	public WindowManager() {

		windows = new HashMap<String, Window>();
		
		setUpdater(new Updater());
		getUpdater().setSkipTime(0.25f);

		if (SceneManager.getInstance().getRenderer() != null) {
		
			SceneManager.getInstance().addFrameListener(getUpdater());
			rootNode = SceneManager.getInstance().getRootSceneNode().createChild(new Vector3F(0,SceneManager.getInstance().getViewport().getHeight(),0));
			
			renderer = new GUIRenderer();
			SceneManager.getInstance().getRenderer().addRenderable(renderer, OpenGLRenderer.OVERLAY_CHANNEL);
		
		}
	}
	
	public boolean exists(String windowName) {
		return windows.containsKey(windowName);
	}
	
	public void setScales(Vector2F[] scales) {
		this.scales = scales;
	}
	
	public void setXScale(int scaleIndex, float xScale) {
		scales[scaleIndex].x = xScale;
	}
	
	public void setYScale(int scaleIndex, float yScale) {
		scales[scaleIndex].y = yScale;
	}
	
	public Vector2F getScale(int scaleIndex) {
		return scales[scaleIndex];
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
		
		if (windows.containsKey(window.getName())) {
			throw new RuntimeException("WindowManager::addWindow Error: Adding window with duplicate name.");
		}
		
		rootNode.attachChild(window.getNode());
		renderer.addWindow(window);
		windows.put(window.getName(), window);
		getUpdater().addItem(window);
		
		notifyResort();
	}
	
	public Window createWindow(String name, Vector2F size) {
		Window window = new Window(name, size);
		addWindow(window);
		return window;
	}
	
	public Window createWindow(String name, ScaledVector2 size) {
		return createWindow(name, size.get());
	}
	
	public Window createWindow(String name, Vector2F pos, Vector2F size) {
		Window window = new Window(name, size);
		addWindow(window);
		window.setRelativePos(pos);
		return window;
	}
	
	public Window createWindow(String name, ScaledVector2 pos, ScaledVector2 size) {
		return createWindow(name, pos.get(), size.get());
	}
	
	public Window createWindow(String name, Vector2F pos, ScaledVector2 size) {
		return createWindow(name, pos, size.get());
	}

	
	public Window createRectWindow(String name, Vector2F pos, Vector2F size, Paint paint) {
		
		Window window = new Window(name, size) {
			@Override
			public float getAlpha() {
				return ((Rectangle2) components.get(0)).getAlpha();
			}
			
			@Override
			public void setAlpha(float alpha) {
				for (int i = 0; i < components.size(); ++i) {
					((Rectangle2) components.get(i)).setAlpha(alpha);
				}
				super.setAlpha(alpha);
			}
			
			@Override
			public void setSize(Vector2F size) {
				
				super.setSize(size);
				
				Viewport viewport = SceneManager.getInstance().getViewport();
				size = new Vector2F(size);
				size.x *= viewport.getWidth();
				size.y *= viewport.getHeight();
				
				for (int i = 0; i < components.size(); ++i) {
					((Rectangle2) components.get(i)).setSize(size);
				}
			}
			
			@Override
			public void setSize(float x, float y) {
				Vector2F size = new Vector2F(x, y);
				this.setSize(size);
			}
		};
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		
		Vector2F rectSize = new Vector2F(size);
		rectSize.x *= viewport.getWidth();
		rectSize.y *= viewport.getHeight();
		
		if (paint.color != null) {
			Rectangle2 rect = new Rectangle2(rectSize, paint.color.copy());
			window.addComponent(rect);
		}
		
		if (paint.borderColor != null) {
			Rectangle2 rect = new Rectangle2(rectSize, paint.borderColor.copy());
			rect.setBorder(paint.borderWidth);
			rect.setFilled(false);
			window.addComponent(rect);
		}
		
		addWindow(window);
		window.setRelativePos(pos);
		return window;
	}
	
	public Window createRectWindow(String name, ScaledVector2 pos, ScaledVector2 size, Paint paint) {
		return createRectWindow(name, pos.get(), size.get(), paint);
	}
	
	public Window createLabel(String name, Vector2F pos, String text) {
		Label label = new Label(name, pos, text);
		addWindow(label);
		return label;
	}
	
	public Window createLabel(String name, ScaledVector2 pos, String text) {
		return createLabel(name, pos.get(), text);
	}
	
	public Button createButton(String name, Vector2F pos, Vector2F size, String text) {
		Button button = new Button(name, pos, size, text);
		addWindow(button);
		return button;
	}
	
	public Button createButton(String name, ScaledVector2 pos, ScaledVector2 size, String text) {
		return createButton(name, pos.get(), size.get(), text);
	}
	
	public Button createButton(String name, Vector2F pos, ScaledVector2 size, String text) {
		return createButton(name, pos, size.get(), text);
	}
	
	public Frame createFrame(String name, Vector2F pos, Vector2F size) {
		Frame frame = new Frame(name, pos, size);
		addWindow(frame);
		return frame;
	}
	
	public Frame createFrame(String name, ScaledVector2 pos, ScaledVector2 size) {
		return createFrame(name, pos.get(), size.get());
	}
	
	public Frame createFrame(String name, Vector2F pos, ScaledVector2 size) {
		return createFrame(name, pos, size.get());
	}
	
	public ImageBox createImageBox(String name, Vector2F pos, String atlasName, String atlasRegion, Vector2F size, Vector2F repeat) {
		ImageBox imageBox = new ImageBox(name, pos, atlasName, atlasRegion, size, repeat);
		addWindow(imageBox);
		return imageBox;
	}
	
	public Window createImageBox(String name, Vector2F pos, String atlasName, String atlasRegion, Vector2F size) {
		return createImageBox(name, pos, atlasName, atlasRegion, size, null);
	}
	
	public Window createImageBox(String name, ScaledVector2 pos, String atlasName, String atlasRegion, ScaledVector2 size) {
		return createImageBox(name, pos.get(), atlasName, atlasRegion, size.get());
	}
	
	public Window createImageBox(String name, Vector2F pos, String atlasName, String atlasRegion, ScaledVector2 size) {
		return createImageBox(name, pos, atlasName, atlasRegion, size.get());
	}
	
	public Window createImageBox(String name, Vector2F pos, String textureName, Vector2F size) {
		ImageBox imageBox = new ImageBox(name, pos, textureName, size);
		addWindow(imageBox);
		return imageBox;
	}
	
	public Window createImageBox(String name, Vector2F pos, int textureHandle, Vector2F size) {
		ImageBox imageBox = new ImageBox(name, pos, textureHandle, size);
		addWindow(imageBox);
		return imageBox;
	}
	
	public Window createImageBox(String name, ScaledVector2 pos, String textureName, ScaledVector2 size) {
		return createImageBox(name, pos.get(), textureName, size.get());
	}
	
	public Window createImageBox(String name, Vector2F pos, String textureName, ScaledVector2 size) {
		return createImageBox(name, pos, textureName, size.get());
	}
	
	public Window createOverlay(String name, Vector2F pos, Vector2F size, Color color) {
		Overlay overlay = new Overlay(name, pos, size, color);
		overlay.setPriority(GUI_OVERLAY_PRIORITY);
		addWindow(overlay);
		return overlay;
	}
	
	public Window createParticleWindow(String name, Vector2F pos, String source) {
		ParticleWindow window = new ParticleWindow(name, pos, source);
		window.setPriority(GUI_OVERLAY_PRIORITY-1);
		addWindow(window);
		return window;
	}
	
	public Window createOverlay(String name, ScaledVector2 pos, ScaledVector2 size, Color color) {
		return createOverlay(name, pos.get(), size.get(), color);
	}
	
	public Window createOverlay(String name, Vector2F pos, ScaledVector2 size, Color color) {
		return createOverlay(name, pos, size.get(), color);
	}
	
	public Window createItemList(String name, Vector2F pos, Vector2F size, float padding, int displayItems) {
		ItemList itemList = new ItemList(name, pos, size, padding, displayItems);
		addWindow(itemList);
		return itemList;
	}
	
	public Window createItemList(String name, ScaledVector2 pos, ScaledVector2 size, float padding, int displayItems) {
		return createItemList(name, pos.get(), size.get(), padding, displayItems);
	}
	
	public Window createItemList(String name, Vector2F pos, ScaledVector2 size, float padding, int displayItems) {
		return createItemList(name, pos, size.get(), padding, displayItems);
	}
	
	public Window createOverlay(String name, Color color) {
		Window overlay = createOverlay(name, new Vector2F(0,-1), new Vector2F(1,1), color);
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
		Window frame = createFrame(name, new Vector2F(skin.MESSAGE_BOX_X, skin.MESSAGE_BOX_Y), new Vector2F(skin.MESSAGE_BOX_W, skin.MESSAGE_BOX_H));
		Window button = createButton(	name + "/ConfirmButton", 
										new Vector2F(skin.MESSAGE_BOX_W/2 - skin.MESSAGE_BOX_W*skin.MESSAGE_BOX_BUTTON_W/2, skin.MESSAGE_BOX_H - skin.MESSAGE_BOX_H*(skin.MESSAGE_BOX_BUTTON_H - skin.MESSAGE_BOX_BUTTON_PAD_Y)),
										new Vector2F(skin.MESSAGE_BOX_W*skin.MESSAGE_BOX_BUTTON_W, skin.MESSAGE_BOX_H*skin.MESSAGE_BOX_BUTTON_H),
										"Confirm");
		frame.addChild(button);
		Label label = (Label)createLabel(name + "/Label", new Vector2F(skin.MESSAGE_BOX_W*skin.MESSAGE_BOX_LABEL_X, skin.MESSAGE_BOX_H*skin.MESSAGE_BOX_LABEL_Y), text);
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
	
	public Window createTooltip(String name, Vector2F size) {
		Window tooltip = new Tooltip(name, size);
		tooltip.setPriority(GUI_OVERLAY_PRIORITY);
		addWindow(tooltip);
		return tooltip;
	}
	
	public Window createTooltip(String name, ScaledVector2 size) {
		return createTooltip(name, size.get());
	}
	
	public void addTextTooltip(Window window, String text) {
		
		String name = window.getName() + "/TooltipText";
		
		String prevName = window.getName() + "/Tooltip";
		String suffix = "";
		
		Window old = null;
		
		if (exists(prevName)) {
			if (!exists(prevName + "2")) {
				old = getWindow( prevName );
				suffix = "2";
				name += suffix;
			} else {
				old = getWindow( prevName + "2");
				suffix = "3";
				name += suffix;
			}
		}
		
		final Window prev = old;
		
		final Label tooltipText = (Label) createLabel(name, skin.TOOLTIP_PADDING.copy(), text);
		tooltipText.setInheritsAlpha(true);
		tooltipText.setInheritsFade(false);
		Vector2F size = tooltipText.getSize();
		if (size.x != 0) {
			size = size.add(skin.TOOLTIP_PADDING.multiply(2));
		} else {
			size.y = 0;
		}
		
		Vector2F pos = new Vector2F();
		if (prev != null) {
			pos.x = prev.getSize().x;
		}
		
		final Window tooltipBg = createRectWindow(window.getName() + "/TooltipBG" + suffix, pos, size, skin.TOOLTIP_PAINT);
		tooltipBg.setInheritsAlpha(true);
		tooltipBg.addChild(tooltipText);
		
		final Tooltip t = (Tooltip) createTooltip(window.getName()+"/Tooltip" + suffix, size);
		t.addChild(tooltipBg);
		t.addChild(tooltipText);
		t.addTarget(window);
		t.setPriority(tooltipText.getPriority()-1);
		
		tooltipBg.setAlpha(0);
		
		if (window.getSizeRelaxation().x <= 1f) {
			window.setSizeRelaxation(new Vector2F(1.3f, 1));
		}
		t.setAlpha(0);
		
		if (prev != null) {
			tooltipText.addEventListener(WindowEventType.FADE_IN_STARTED, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					Vector2F size = prev.getSize();
					
					if (prev != null) {
						Vector2F pos = prev.getRelativePos();
						t.setRelativePos(new Vector2F(pos.x + size.x, pos.y));
					}
				}
			});
		}
		
		tooltipText.addEventListener(WindowEventType.SIZE_CHANGED, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				Vector2F size = tooltipText.getSize();
				
				if (prev != null) {
					Vector2F pos = prev.getRelativePos();
					t.setRelativePos(new Vector2F(pos.x + prev.getSize().x, pos.y));
				}
				
				if (size.x != 0) {
					size = size.add(skin.TOOLTIP_PADDING.multiply(2));
				} else {
					size.y = 0;
				}
				
				Viewport vp = SceneManager.getInstance().getViewport();
				tooltipBg.setSize(size);
				t.setSize(size);
				size = new Vector2F(size.x * vp.getWidth(), size.y * vp.getHeight());
				
				((Rectangle2)tooltipBg.getComponent(0)).setSize(size);
				((Rectangle2)tooltipBg.getComponent(1)).setSize(size);
			}
		});
	}
	
	public Window getTooltip(Window window) {
		return getWindow(window.getName() + "/TooltipText");
	}
	
	public Window createProgressBar(String name, Vector2F pos, Vector2F size, float maxProgress) {
		Window progressBar = new ProgressBar(name, pos, size, maxProgress);
		addWindow(progressBar);
		return progressBar;
	}
	
	public Window createProgressBar(String name, ScaledVector2 pos, ScaledVector2 size, float maxProgress) {
		return createProgressBar(name, pos.get(), size.get(), maxProgress);
	}
	
	public Window createProgressBar(String name, Vector2F pos, ScaledVector2 size, float maxProgress) {
		return createProgressBar(name, pos, size.get(), maxProgress);
	}
	
	public void removeWindow(Window window) {
		renderer.removeWindow(window);
		windows.remove(window.getName());
		window.getNode().detach();
		getUpdater().removeItem(window);
		InputManager.getInstance().removeTouchListener(window);
	}
	
	public Window getWindow(String name) {
		return windows.get(name);
	}
	
	public SceneNode getRootNode() {
		return rootNode;
	}

	public void notifyResort() {
		renderer.notifyResort();
	}

	public Updater getUpdater() {
		return updater;
	}

	public void setUpdater(Updater updater) {
		this.updater = updater;
	}


}
	