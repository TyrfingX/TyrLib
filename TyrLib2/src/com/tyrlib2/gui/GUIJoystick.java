package com.tyrlib2.gui;

import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.input.IJoystickListener;
import com.tyrlib2.input.Joystick;
import com.tyrlib2.math.Vector2;

public class GUIJoystick implements IJoystickListener{
	
	private Joystick joystick;
	
	private ImageBox bg;
	private ImageBox current;
	
	private Vector2 currentBasePoint;
	
	public GUIJoystick(String name, Joystick joystick) {
		this.joystick = joystick;
		joystick.addListener(this);
		
		float size = joystick.getMaxDistance()*2;
		
		Vector2 windowSize = new Vector2(size, size*SceneManager.getInstance().getViewportRatio());
		
		bg = (ImageBox) WindowManager.getInstance().createImageBox(name + "/Bg", new Vector2(), "GUI", "JOYSTICK_BG", windowSize);
		
		float currentSize = size / 2;
		Vector2 currentWindowSize = new Vector2(currentSize, currentSize * SceneManager.getInstance().getViewportRatio());
		current = (ImageBox) WindowManager.getInstance().createImageBox(name + "/Current", new Vector2(), "GUI", "JOYSTICK", currentWindowSize);
		bg.addChild(current);
		
		bg.setAlpha(0);
		bg.setVisible(false);
		
		current.setReceiveTouchEvents(false);
		bg.setReceiveTouchEvents(false);
	}

	@Override
	public void onJoystickActivated() {
		Vector2 bgPos = new Vector2(joystick.getBasePoint());
		bgPos.x -= bg.getSize().x/2;
		bgPos.y -= bg.getSize().y/2;
		bg.setRelativePos(bgPos);
		
		currentBasePoint = new Vector2(bg.getSize().x/2 - current.getSize().x/2, bg.getSize().y/2 - current.getSize().y/2);
		current.setRelativePos(currentBasePoint);
		
		bg.fadeIn(1, 0.5f);
	}

	@Override
	public void onJoystickDeactivated() {
		bg.fadeOut(0, 0.5f);
	}

	@Override
	public void onJoystickMoved(Vector2 movement) {
		current.setRelativePos(currentBasePoint.add(movement));
	}
}
