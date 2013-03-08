package com.tyrlib2.gui;

import java.util.PriorityQueue;
import java.util.Queue;

import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.util.ReversePriorityComparator;

/**
 * This takes care of rendering the GUI
 * @author Sascha
 *
 */

public class GUIRenderer implements IRenderable {
	
	private Queue<Window> windows; 
	
	public GUIRenderer() {
		windows = new PriorityQueue<Window>(100, new ReversePriorityComparator());
	}
	
	@Override
	public void render(float[] vpMatrix) {
		for (Window window : windows) {
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

}
