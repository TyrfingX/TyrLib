package com.tyrlib2.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.math.Rectangle;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.PriorityComparator;

/**
 * This takes care of rendering the GUI
 * @author Sascha
 *
 */

public class GUIRenderer implements IRenderable {
	
	private List<Window> windows; 
	private boolean resort = false;
	private int insertionID;
	
	public GUIRenderer() {
		windows = new ArrayList<Window>();
	}
	
	@Override
	public void render(float[] vpMatrix) {
		
		if (resort) {
			resort = false;
			Collections.sort(windows, new PriorityComparator());
		}
		
		for (int i = 0, countWindows = windows.size(); i < countWindows; ++i) {
			Window window = windows.get(i);
			if (window.isVisible() && window.getAlpha() > 0) {
				float posX = window.getAbsolutePosX();
				float posY = window.getAbsolutePosY();
				Vector2 size = window.getSize();
				if (Rectangle.overlap(posX, posY, posX + size.x, posY + size.y, 0.0f, 0.0f, 1.0f, 1.0f)) {
					window.render(vpMatrix);
				}
			}
		}

	}
	
	public void addWindow(Window window) {
		windows.add(window);
	}
	
	public void removeWindow(Window window) {
		windows.remove(window);
	}
	
	public void notifyResort() {
		resort = true;
	}

	@Override
	public void renderShadow(float[] vpMatrix) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setInsertionID(int id) {
		this.insertionID = id;
	}

	@Override
	public int getInsertionID() {
		return insertionID;
	}

}
