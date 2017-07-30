package com.TyrLib2.PC.main;

import com.tyrlib2.graphics.scene.SceneManager;

public class ConsoleView extends Thread {
	
	private ConsoleRenderer renderer;
	
	public ConsoleView() {
		renderer = new ConsoleRenderer(true);
		SceneManager.getInstance().setRenderer(renderer);
	}
	
	@Override
	public void run() {
		while (true) {
			renderer.render();
		}
	}
}
