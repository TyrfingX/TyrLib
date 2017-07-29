package com.tyrfing.games.tyrlib3.view.gui.widgets;

import com.tyrfing.games.tyrlib3.edit.input.IJoystickListener;
import com.tyrfing.games.tyrlib3.edit.input.Joystick;
import com.tyrfing.games.tyrlib3.model.math.Vector2F;
import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;
import com.tyrfing.games.tyrlib3.view.gui.WindowManager;

public class GUIJoystick implements IJoystickListener{
	
	private Joystick joystick;
	
	private ImageBox bg;
	private ImageBox current;
	
	private Vector2F currentBasePoint;
	
	public GUIJoystick(String name, Joystick joystick) {
		this.joystick = joystick;
		joystick.addListener(this);
		
		float size = joystick.getMaxDistance()*2;
		
		Vector2F windowSize = new Vector2F(size, size*SceneManager.getInstance().getViewportRatio());
		
		bg = (ImageBox) WindowManager.getInstance().createImageBox(name + "/Bg", new Vector2F(), "GUI", "JOYSTICK_BG", windowSize);
		
		float currentSize = size / 2;
		Vector2F currentWindowSize = new Vector2F(currentSize, currentSize * SceneManager.getInstance().getViewportRatio());
		current = (ImageBox) WindowManager.getInstance().createImageBox(name + "/Current", new Vector2F(), "GUI", "JOYSTICK", currentWindowSize);
		bg.addChild(current);
		
		bg.setAlpha(0);
		bg.setVisible(false);
		
		current.setReceiveTouchEvents(false);
		bg.setReceiveTouchEvents(false);
	}

	@Override
	public void onJoystickActivated() {
		Vector2F bgPos = new Vector2F(joystick.getBasePoint());
		bgPos.x -= bg.getSize().x/2;
		bgPos.y -= bg.getSize().y/2;
		bg.setRelativePos(bgPos);
		
		currentBasePoint = new Vector2F(bg.getSize().x/2 - current.getSize().x/2, bg.getSize().y/2 - current.getSize().y/2);
		current.setRelativePos(currentBasePoint);
		
		bg.fadeIn(1, 0.5f);
	}

	@Override
	public void onJoystickDeactivated() {
		bg.fadeOut(0, 0.5f);
	}

	@Override
	public void onJoystickMoved(Vector2F movement) {
		current.setRelativePos(currentBasePoint.add(movement));
	}
}
