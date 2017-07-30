package com.tyrfing.games.id17.gui;

import com.tyrlib2.graphics.renderables.Image2;
import com.tyrlib2.graphics.renderables.Rectangle2;
import com.tyrlib2.graphics.renderer.TextureAtlas;
import com.tyrlib2.graphics.renderer.Viewport;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ItemListEntry;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;

public abstract class DefaultItemListEntry extends ItemListEntry {

	public enum BG_TYPE {
		IMAGE, RECT;
	}
	
	private boolean click;
	private Vector2 clickPoint;
	protected Image2 image;
	protected Window rect;
	
	public static final float MAX_DISTANCE = 0.01f;
	
	private boolean enabled;
	protected boolean activated;
	
	private String disabledText;
	
	public DefaultItemListEntry(String name, Vector2 size, String disabledText, BG_TYPE type) {
		super(name, size);
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		if (type == BG_TYPE.IMAGE) {
			TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("MAIN_GUI");
			image = new Image2(new Vector2(size.x * viewport.getWidth(), size.y * viewport.getHeight()), atlas.getTexture(), atlas.getRegion("PAPER2"));
			addComponent(image);
		} else {
			rect = WindowManager.getInstance().createRectWindow(name + "/RECTBG", new Vector2(), size, TabGUI.GOLDEN_WOOD_PAINT2);
			addChild(rect);
		}
		
		this.addEventListener(WindowEventType.TOUCH_LEAVES, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (click) {
					TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("MAIN_GUI");
					if (image != null) {
						image.setTextureRegion( atlas.getRegion("PAPER2") );
					}
					click = false;
				}
			}
		});
		
		this.addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (enabled && !activated) {
					click = true;
					if (image != null) {
						TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("MAIN_GUI");
						image.setTextureRegion( atlas.getRegion("PAPER2_ACTIVE") );
					}
				}
				clickPoint = new Vector2((Vector2) event.getParam("POINT"));
			}
		});
		
		this.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (click) {
					click = false;
					onClick();
				}
			}
		});
		
		this.addEventListener(WindowEventType.TOUCH_MOVES, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (click) {
					Vector2 current = new Vector2((Vector2) event.getParam("POINT"));
					if (current.vectorTo(clickPoint).length() >= MAX_DISTANCE) {
						click = false;
						if (image != null) {
							TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("MAIN_GUI");
							image.setTextureRegion( atlas.getRegion("PAPER2") );
						}
					}
				}
			}
		});
		
		if (disabledText != null) {
			WindowManager.getInstance().addTextTooltip(this, disabledText);
		}
		
		this.setPassTouchEventsThrough(true);
		this.setReceiveTouchEvents(true);
	}
	
	public DefaultItemListEntry(String name, Vector2 size, String disabledText) {
		this(name, size, disabledText, BG_TYPE.IMAGE);
	}
	
	@Override
	public float getAlpha() {
		if (image != null) {
			return image.getAlpha();
		} else {
			return rect.getAlpha();
		}
	}
	
	@Override
	public void setAlpha(float alpha) {
		
		alpha = Math.min(1, alpha);
		alpha = Math.max(0, alpha);
		
		if (image != null) {
			image.setAlpha(alpha);
		}
		
		if (rect != null) {
			((Rectangle2)rect.getComponent(0)).setAlpha(alpha);
			((Rectangle2)rect.getComponent(1)).setAlpha(alpha);
		}
		
		super.setAlpha(alpha);
	}
	
	public void setEnabled(boolean enabled) {
		if (!enabled) {
			if (image != null) {
				TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("MAIN_GUI");
				image.setTextureRegion(atlas.getRegion("PAPER2_DISABLED"));
			}
		} else {
			if (image != null) {
				TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("MAIN_GUI");
				image.setTextureRegion(atlas.getRegion("PAPER2"));
			}
		}
		
		this.enabled = enabled;
	}
	
	@Override
	public boolean isEnabled() {
		return super.isEnabled() && enabled;
	}
	
	protected abstract void onClick();
	
	public void unhighlight() {
		if (activated) {
			activated = false;
			if (image != null) {
				TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("MAIN_GUI");
				image.setTextureRegion( atlas.getRegion("PAPER2") );
			}
		}
	}
	
	public void highlight() {
		if (image != null) {
			TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("MAIN_GUI");
			image.setTextureRegion( atlas.getRegion("PAPER2_ACTIVE") );
		}
		activated = true;
	}

}
