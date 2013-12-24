package com.tyrlib2.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.util.PriorityComparator;

/**
 * This takes care of rendering the GUI
 * @author Sascha
 *
 */

public class GUIRenderer implements IRenderable {
	
	private List<Window> windows; 
	private boolean resort = false;
	
	public GUIRenderer() {
		windows = new ArrayList<Window>();
	}
	
	@Override
	public void render(float[] vpMatrix) {
		
		if (resort) {
			resort = false;
			Collections.sort(windows, new PriorityComparator());
		}
		
		for (int i = 0; i < windows.size(); ++i) {
			Window window = windows.get(i);
			if (window.isVisible()) {
				window.render(vpMatrix);
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

}
