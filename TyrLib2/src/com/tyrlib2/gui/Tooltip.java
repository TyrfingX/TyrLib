package com.tyrlib2.gui;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.math.Vector2;

public class Tooltip extends Window {

	private class AppearOnTouch implements IEventListener {
		@Override
		public void onEvent(WindowEvent event) {
			Vector2 v = (Vector2)event.getParam("POINT");
			if (v != null) {
				Vector2 pos = new Vector2(v);
				if (v.x >= 0.5f) {
					pos.x -= getSize().x + (TyrGL.GL_USE_VBO == 1 ? 0 : 0.075f);
				} else {
					pos.x += (TyrGL.GL_USE_VBO == 1 ? 0 : 0.095f);
				}
				pos.y = -pos.y;
				setRelativePos(pos);
				fadeIn(getMaxAlpha(), fadeTime);
				setVisible(true);
			}
		}
	}
	
	private class DisappearOnTouch implements IEventListener {
		@Override
		public void onEvent(WindowEvent event) {
			fadeOut(0, fadeTime);
		}
	}
	
	private class TargetDestroyed implements IEventListener {
		@Override
		public void onEvent(WindowEvent event) {
			Window source = event.getSource();
			targets.remove(source);
		}
	}
		
	private float fadeTime;
	
	private AppearOnTouch appearOnTouch = new AppearOnTouch();
	private DisappearOnTouch disappearOnTouch = new DisappearOnTouch();
	private TargetDestroyed targetDestroyed = new TargetDestroyed();
	private List<Window> targets = new ArrayList<Window>();
	
	public Tooltip(String name, Vector2 size) {
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
		targets.add(window);
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		for (int i = 0; i < targets.size(); ++i) {
			targets.get(i).removeEventListener(WindowEventType.TOUCH_ENTERS, appearOnTouch);
			targets.get(i).removeEventListener(WindowEventType.TOUCH_LEAVES, disappearOnTouch);
			targets.get(i).removeEventListener(WindowEventType.MOUSE_ENTERS, appearOnTouch);
			targets.get(i).removeEventListener(WindowEventType.MOUSE_LEAVES, disappearOnTouch);
			targets.get(i).removeEventListener(WindowEventType.DESTROYED, targetDestroyed);
		}
	}
}
