package com.tyrfing.games.tyrlib3.pc.graphics.renderer;

import com.tyrfing.games.tyrlib3.graphics.scene.SceneManager;

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
