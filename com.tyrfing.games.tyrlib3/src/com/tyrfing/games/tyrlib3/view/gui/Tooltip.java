package com.tyrfing.games.tyrlib3.view.gui;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.tyrlib3.graphics.renderer.TyrGL;
import com.tyrfing.games.tyrlib3.math.Vector2F;
import com.tyrfing.games.tyrlib3.view.gui.WindowEvent.WindowEventType;

public class Tooltip extends Window {

	private class AppearOnTouch implements IEventListener {
		@Override
		public void onEvent(WindowEvent event) {
			Vector2F v = (Vector2F)event.getParam("POINT");
			if (v != null) {
				Vector2F pos = new Vector2F(v);
				if (v.x >= 0.5f) {
					pos.x -= getSize().x + (TyrGL.TARGET == TyrGL.PC_TARGET ? 0 : 0.075f);
				} else {
					pos.x += (TyrGL.TARGET == TyrGL.PC_TARGET ? 0 : 0.095f);
				}
				
				if (v.y <= 0.5f) {
					pos.y += getSize().y;
				}
				
				pos.y = -pos.y;
				setRelativePos(pos);
				currentTarget = event.getSource();
				fadeIn(getMaxAlpha(), fadeTime);
				setPriority(event.getSource().getPriority()*10);
				setVisible(true);
			}
		}
	}
	
	private class DisappearOnTouch implements IEventListener {
		@Override
		public void onEvent(WindowEvent event) {
			fadeOut(0, fadeTime);
			currentTarget = null;
		}
	}
	
	private class TargetDestroyed implements IEventListener {
		@Override
		public void onEvent(WindowEvent event) {
			removeTarget(event.getSource());
			fadeOut(0, fadeTime);
			currentTarget = null;
		}
	}
	
	private class TargetAlphaChanged implements IEventListener {
		@Override
		public void onEvent(WindowEvent event) {
			if (event.getSource().getAlpha() == 0) {
				fadeOut(0, fadeTime);
				currentTarget = null;
			}
		}
	}
	
	private class TargetFadeOutStarted implements IEventListener {
		@Override
		public void onEvent(WindowEvent event) {
			fadeOut(0, fadeTime);
			currentTarget = null;
		}
	}
	
	private class TargetVisibilityChanged implements IEventListener {
		@Override
		public void onEvent(WindowEvent event) {
			if (!event.getSource().isVisible()) {
				fadeOut(0, fadeTime);
				currentTarget = null;
			}
		}
	}
		
	private float fadeTime;
	
	private AppearOnTouch appearOnTouch = new AppearOnTouch();
	private DisappearOnTouch disappearOnTouch = new DisappearOnTouch();
	private TargetDestroyed targetDestroyed = new TargetDestroyed();
	private TargetAlphaChanged targetAlphaChanged = new TargetAlphaChanged();
	private TargetFadeOutStarted targetFadeOutStarted = new TargetFadeOutStarted();
	private TargetVisibilityChanged targetVisibilityChanged = new TargetVisibilityChanged();
	private List<Window> targets = new ArrayList<Window>();
	private Window currentTarget;
	
	public Tooltip(String name, Vector2F size) {
		super(name, size);
		
		Skin skin = WindowManager.getInstance().getSkin();
		this.setMaxAlpha(skin.TOOLTIP_MAX_ALPHA);
		
		fadeTime = skin.TOOLTIP_FADE_TIME;
		
		setAlpha(0);
	}
	
	public void addTarget(Window window) {
		window.addEventListener(WindowEventType.TOUCH_ENTERS, appearOnTouch);
		window.addEventListener(WindowEventType.MOUSE_ENTERS, appearOnTouch);
		window.addEventListener(WindowEventType.TOUCH_LEAVES, disappearOnTouch);
		window.addEventListener(WindowEventType.MOUSE_LEAVES, disappearOnTouch);
		window.addEventListener(WindowEventType.DESTROYED, targetDestroyed);
		window.addEventListener(WindowEventType.ALPHA_CHANGED, targetAlphaChanged);
		window.addEventListener(WindowEventType.FADE_OUT_STARTED, targetFadeOutStarted);
		window.addEventListener(WindowEventType.VISIBILITY_CHANGED, targetVisibilityChanged);
		targets.add(window);
		
		this.addEventListener(WindowEventType.DESTROYED, new DestroyOnEvent());
	}
	
	public void removeTarget(Window window) {
		window.removeEventListener(WindowEventType.TOUCH_ENTERS, appearOnTouch);
		window.removeEventListener(WindowEventType.TOUCH_LEAVES, disappearOnTouch);
		window.removeEventListener(WindowEventType.MOUSE_ENTERS, appearOnTouch);
		window.removeEventListener(WindowEventType.MOUSE_LEAVES, disappearOnTouch);
		window.removeEventListener(WindowEventType.DESTROYED, targetDestroyed);
		window.removeEventListener(WindowEventType.ALPHA_CHANGED, targetAlphaChanged);
		window.removeEventListener(WindowEventType.FADE_OUT_STARTED, targetFadeOutStarted);
		window.removeEventListener(WindowEventType.VISIBILITY_CHANGED, targetVisibilityChanged);
		targets.remove(window);
	}
	
	@Override
	public float getAlpha() {
		return this.getChild(0).getAlpha();
	}
	
	public Window getCurrentTarget() {
		return currentTarget;
	}
	
	@Override
	public void onUpdate(float time) {
		super.onUpdate(time);
		if (currentTarget != null) {
			if (isVisible() && blendState == BLEND_STATE.IDLE && !currentTarget.isBeingTouched()) {
				fadeOut(0, fadeTime);
			}
		}
	}
}
