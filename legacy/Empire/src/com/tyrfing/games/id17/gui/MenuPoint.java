package com.tyrfing.games.id17.gui;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.gui.Window;

public abstract class MenuPoint implements GUI {
	
	protected boolean visible;
	protected List<Window> mainElements = new ArrayList<Window>();
	
	public void show() {
		visible = true;
		
		for (int i = 0; i < mainElements.size(); ++i) {
			mainElements.get(i).fadeIn(1, TabGUI.DISPLAY_TIME);
		}
	}
	
	@Override
	public void hide() {
		visible = false;
		
		for (int i = 0; i < mainElements.size(); ++i) {
			mainElements.get(i).fadeOut(0, TabGUI.DISPLAY_TIME);
		}
	}

	@Override
	public boolean isVisible() {
		return visible;
	}
	
	public abstract void update();
}
