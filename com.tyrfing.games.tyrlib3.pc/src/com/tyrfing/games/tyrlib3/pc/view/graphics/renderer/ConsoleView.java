package com.tyrfing.games.tyrlib3.pc.view.graphics.renderer;

import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;

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
