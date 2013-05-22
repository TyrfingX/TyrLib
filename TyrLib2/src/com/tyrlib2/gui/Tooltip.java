package com.tyrlib2.gui;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.math.Vector2;

public class Tooltip extends Window {

	private class AppearOnTouch implements IEventListener {
		@Override
		public void onEvent(WindowEvent event) {
			Window source = event.getSource();
			Vector2 pos = source.getAbsolutePos();
			pos.x += source.getSize().x;
			pos.y = (1-pos.y) + source.getSize().y;
			setRelativePos(pos);
			fadeIn(getMaxAlpha(), fadeTime);
			setVisible(true);
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
		
	private Frame frame;
	private float fadeTime;
	
	private AppearOnTouch appearOnTouch = new AppearOnTouch();
	private DisappearOnTouch disappearOnTouch = new DisappearOnTouch();
	private TargetDestroyed targetDestroyed = new TargetDestroyed();
	
	private List<Window> targets = new ArrayList<Window>();
	
	public Tooltip(String name, Vector2 size) {
		super(name, size);
		
		frame = WindowManager.getInstance().createFrame(name + "/frame", new Vector2(), size);
		addChild(frame);
		
		Skin skin = WindowManager.getInstance().getSkin();
		this.setMaxAlpha(skin.TOOLTIP_MAX_ALPHA);
		
		fadeTime = skin.TOOLTIP_FADE_TIME;

		setAlpha(0);
	
		setReceiveTouchEvents(false);
		frame.setReceiveTouchEvents(false);
	}
	
	public Frame getFrame() {
		return frame;
	}
	
	public void addTarget(Window window) {
		window.addEventListener(WindowEventType.TOUCH_ENTERS, appearOnTouch);
		window.addEventListener(WindowEventType.TOUCH_LEAVES, disappearOnTouch);
		window.addEventListener(WindowEventType.DESTROYED, targetDestroyed);
		targets.add(window);
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		for (int i = 0; i < targets.size(); ++i) {
			targets.get(i).removeEventListener(WindowEventType.TOUCH_ENTERS, appearOnTouch);
			targets.get(i).removeEventListener(WindowEventType.TOUCH_LEAVES, disappearOnTouch);
			targets.get(i).removeEventListener(WindowEventType.DESTROYED, targetDestroyed);
		}
	}

	@Override
	public float getAlpha() {
		return frame.getAlpha();
	}
}
